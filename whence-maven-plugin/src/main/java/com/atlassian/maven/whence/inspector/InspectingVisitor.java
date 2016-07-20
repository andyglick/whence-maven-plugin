package com.atlassian.maven.whence.inspector;

import com.atlassian.maven.whence.data.PackageInfo;
import com.atlassian.maven.whence.data.PackageMapper;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

public class InspectingVisitor {

    private PackageMapper.Builder packageMapperBuilder = PackageMapper.builder();
    private JarInspector jarInspector = new JarInspector();

    public PackageMapper inspect(DependencyNode rootNode, ArtifactResolution artifactResolution) {

        DependencyNodeVisitor visitor = new DependencyNodeVisitor() {
            @Override
            public boolean endVisit(DependencyNode node) {
                return true;
            }

            @Override
            public boolean visit(DependencyNode node) {
                if (node.getParent() != null) {
                    inspectNode(node, artifactResolution);
                }
                return true;
            }
        };

        rootNode.accept(visitor);

        return packageMapperBuilder.build();
    }

    private void inspectNode(DependencyNode node, ArtifactResolution artifactResolution) {
        PackageInfo info = jarInspector.inspect(node, artifactResolution);

        Artifact artifact = artifactResolution.resolveArtifact(node.getArtifact());
        packageMapperBuilder.addPackage(artifact, info);
    }
}
