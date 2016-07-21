package com.atlassian.maven.whence.inspector;

import com.atlassian.maven.whence.data.PackageInfo;
import com.atlassian.maven.whence.data.PackageMapper;
import com.atlassian.maven.whence.reporting.Reporter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

public class InspectingVisitor {

    private PackageMapper.Builder packageMapperBuilder = PackageMapper.builder();
    private JarInspector jarInspector = new JarInspector();

    public PackageMapper inspect(DependencyNode rootNode, ArtifactResolution artifactResolution, Reporter.ReportDetail reportDetail, Log log) {

        DependencyNodeVisitor visitor = new DependencyNodeVisitor() {
            @Override
            public boolean endVisit(DependencyNode node) {
                return true;
            }

            @Override
            public boolean visit(DependencyNode node) {
                inspectNode(node, artifactResolution, reportDetail, log);
                return true;
            }
        };

        rootNode.accept(visitor);

        return packageMapperBuilder.build();
    }

    private void inspectNode(DependencyNode node, ArtifactResolution artifactResolution, Reporter.ReportDetail reportDetail, Log log) {
        long then = now();
        Artifact artifact = artifactResolution.resolveArtifact(node.getArtifact());

        PackageInfo info = jarInspector.inspect(node, artifactResolution, reportDetail);

        log.info(String.format("\t\t'%s' (%s ms)", artifact, now() - then));

        packageMapperBuilder.addPackage(artifact, info);
    }

    private long now() {
        return System.currentTimeMillis();
    }
}
