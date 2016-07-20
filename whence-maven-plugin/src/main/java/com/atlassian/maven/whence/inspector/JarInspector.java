package com.atlassian.maven.whence.inspector;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Jar;
import com.atlassian.maven.whence.data.PackageInfo;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.CollectingDependencyNodeVisitor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

class JarInspector {

    PackageInfo inspect(DependencyNode depGraph, ArtifactResolution artifactResolution) {
        Artifact artifact = artifactResolution.resolveArtifact(depGraph.getArtifact());
        Jar[] classPath = buildClassPath(depGraph, artifactResolution);
        Jar jar = makeJar(artifact);

        Builder bndBuilder = makeBndBuilder(jar, classPath);
        try {
            bndBuilder.analyze();

            Manifest manifest = jar.getManifest();

            return PackageInfo.builder(artifact)
                    .setContains(bndBuilder.getContained())
                    .setReferences(bndBuilder.getReferred())
                    .setImports(readImports(manifest))
                    .setExports(readExports(manifest))
                    .build();

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
        Attributes mainAttributes = manifest.getMainAttributes();
        return Optional.ofNullable(mainAttributes.getValue(name));
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
            //bndBuilder.setBase(getBase(project));
        }
        return bndBuilder;
    }


    private Jar makeJar(Artifact artifact) {
        File file = getFile(artifact);
        try {
            return new Jar(artifact.getArtifactId(), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private File getFile(Artifact artifact) {
        return artifact.getFile();
    }

}
