package com.atlassian.maven.whence.data;

import java.util.Objects;

public class ClassKey {
    private final String fqcn;
    private final ArtifactKey artifact;

    public ClassKey(String fqcn, ArtifactKey artifact) {
        this.fqcn = fqcn;
        this.artifact = artifact;
    }

    public ArtifactKey getArtifact() {
        return artifact;
    }

    public String getFQCN() {
        return fqcn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassKey classKey = (ClassKey) o;
        return Objects.equals(fqcn, classKey.fqcn) &&
                Objects.equals(artifact, classKey.artifact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fqcn, artifact);
    }
}
