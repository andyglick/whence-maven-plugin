package com.atlassian.maven.whence.inspector;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Packages;
import com.atlassian.maven.whence.data.PackageInfo;
import com.atlassian.maven.whence.reporting.Reporter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.CollectingDependencyNodeVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

class JarInspector {

    PackageInfo inspect(DependencyNode depGraph, ArtifactResolution artifactResolution, Reporter.ReportDetail reportDetail) {
        Artifact artifact = artifactResolution.resolveArtifact(depGraph.getArtifact());
        Jar[] classPath = buildClassPath(depGraph, artifactResolution);
        Jar jar = makeJar(artifact);

        PackageInfo.Builder builder = PackageInfo.builder(artifact);
        try {
            Manifest manifest = jar.getManifest();
            Builder bndBuilder = makeBndBuilder(jar, classPath);
            bndBuilder.analyze();
            if (reportDetail == Reporter.ReportDetail.EXPORTS) {
                return builder
                        .setImports(readImports(manifest))
                        .setExports(readExports(manifest))
                        .setApi(readApi(bndBuilder))
                        .build();
            } else {
                return builder
                        .setApi(readApi(bndBuilder))
                        .setContains(bndBuilder.getContained())
                        .setReferences(bndBuilder.getReferred())
                        .setImports(readImports(manifest))
                        .setExports(readExports(manifest))
                        .build();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Parameters readExports(Manifest manifest) {
        Optional<String> value = readManifest(manifest, "Export-Package");
        return getParameters(value);
    }

    private Parameters readImports(Manifest manifest) {
        Optional<String> value = readManifest(manifest, "Import-Package");
        return getParameters(value);
    }

    private Parameters getParameters(Optional<String> value) {
        return value.map(Parameters::new).orElse(new Parameters());
    }

    private Optional<String> readManifest(Manifest manifest, String name) {
        return ofNullable(manifest)
                .map(Manifest::getMainAttributes)
                .map(attributes -> ofNullable(attributes.getValue(name)))
                .flatMap(opString -> opString);
    }

    private Packages readApi(Builder bndBuilder) {
        Packages packages = new Packages();
        Map<Descriptors.PackageRef, List<Descriptors.PackageRef>> apiUses = bndBuilder.getAPIUses();
        for (Descriptors.PackageRef packageRef : apiUses.keySet()) {
            packages.put(packageRef);
        }
        return packages;
    }

    private Jar[] buildClassPath(DependencyNode depGraph, ArtifactResolution artifactResolution) {
        CollectingDependencyNodeVisitor collector = new CollectingDependencyNodeVisitor();
        for (DependencyNode dependencyNode : depGraph.getChildren()) {
            dependencyNode.accept(collector);
        }
        List<Jar> jars = collector.getNodes().stream()
                .map(node -> artifactResolution.resolveArtifact(node.getArtifact()))
                .map(this::makeJar)
                .collect(Collectors.toList());
        return jars.toArray(new Jar[jars.size()]);
    }

    private Builder makeBndBuilder(Jar jar, Jar[] classpath) {
        Builder bndBuilder = new Builder();
        synchronized (this) {
            bndBuilder.setJar(jar);
            bndBuilder.setClasspath(classpath);
        }
        return bndBuilder;
    }


    private Jar makeJar(Artifact artifact) {
        File file = getFile(artifact);
        try {
            if (!file.exists()) {
                mkdirMinusP(file);
            }
            return new Jar(artifact.getArtifactId(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mkdirMinusP(File file) {
        try {
            Files.createDirectories(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private File getFile(Artifact artifact) {
        return artifact.getFile();
    }

}
