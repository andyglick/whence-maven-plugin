package com.atlassian.maven.whence.inspector;

import com.atlassian.maven.whence.data.ArtifactKey;
import org.apache.maven.artifact.Artifact;

import java.util.Optional;
import java.util.Set;

/**
 * I cant work out how to do this in a dependency graph node.getArtifact() so we swap them
 * from the project.getArtifacts()
 *
 * Maven - its puzzling.
 */
public class ArtifactResolution {

    private final Set<Artifact> resolvedArtifacts;

    public ArtifactResolution(Set<Artifact> resolvedArtifacts) {
        this.resolvedArtifacts = resolvedArtifacts;
    }

    public Artifact resolveArtifact(Artifact artifact) {
        Optional<Artifact> any = resolvedArtifacts.stream().filter(a -> ArtifactKey.artifactEquals(a, artifact)).findAny();
        if (!any.isPresent()) {
            throw new IllegalStateException("Unable to resolve artifact :" + artifact);
        }
        return any.get();
    }

}
