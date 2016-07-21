package com.atlassian.maven.whence.inspector;

import com.atlassian.maven.whence.data.ArtifactKey;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;

import java.io.File;
import java.util.Optional;
import java.util.Set;

/**
 * I cant work out how to do this in a dependency graph node.getArtifact() so we swap them
 * from the project.getArtifacts()
 *
 * Maven - its puzzling.
 */
public class ArtifactResolution {

    private final Artifact rootArtifact;
    private final Set<Artifact> resolvedArtifacts;

    public ArtifactResolution(Artifact rootArtifact, File outputDirectory, Set<Artifact> resolvedArtifacts) {
        this.rootArtifact = rootArtifact(rootArtifact, outputDirectory);
        this.resolvedArtifacts = resolvedArtifacts;
    }

    public Artifact resolveArtifact(Artifact artifact) {
        if (ArtifactKey.artifactEquals(rootArtifact, artifact)) {
            return rootArtifact;
        }
        Optional<Artifact> any = resolvedArtifacts.stream().filter(a -> ArtifactKey.artifactEquals(a, artifact)).findAny();
        if (!any.isPresent()) {
            throw new IllegalStateException("Unable to resolve artifact :" + artifact);
        }
        return any.get();
    }

    private Artifact rootArtifact(final Artifact artifact, File outputDirectory) {
        return new DefaultArtifact(artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getVersion(),
                artifact.getScope(),
                artifact.getType(),
                artifact.getClassifier(),
                artifact.getArtifactHandler()) {
            @Override
            public File getFile() {
                return outputDirectory;
            }
        };
    }

}
