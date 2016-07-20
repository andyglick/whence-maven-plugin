package com.atlassian.maven.whence.data;

import org.apache.maven.artifact.Artifact;

import java.util.Objects;

/**
 * The Maven artifact class is not able to be put into a HashMap (boo)
 */
public class ArtifactKey implements Comparable<ArtifactKey> {
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final Artifact artifact;

    ArtifactKey(Artifact artifact) {
        this.artifact = artifact;
        groupId = artifact.getGroupId();
        artifactId = artifact.getArtifactId();
        version = artifact.getVersion();
    }

    private Artifact getArtifact() {
        return artifact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtifactKey that = (ArtifactKey) o;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(artifactId, that.artifactId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }

    @Override
    public int compareTo(ArtifactKey o) {
        return this.getArtifact().compareTo(o.getArtifact());
    }

    @Override
    public String toString() {
        return artifact.toString();
    }


    public static boolean artifactEquals(Artifact a1, Artifact a2) {
        return new ArtifactKey(a1).equals(new ArtifactKey(a2));
    }

}
