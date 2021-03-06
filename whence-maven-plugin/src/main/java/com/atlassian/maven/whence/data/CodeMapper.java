package com.atlassian.maven.whence.data;

import org.apache.maven.artifact.Artifact;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class CodeMapper {


    private final Map<ArtifactKey, PackageInfo> artifactsToPackageInfo;

    private CodeMapper(Map<ArtifactKey, PackageInfo> artifactsToPackageInfo) {
        this.artifactsToPackageInfo = artifactsToPackageInfo;
    }

    public PackageInfo getPackageInfoFor(Artifact artifact) {
        return artifactsToPackageInfo.get(new ArtifactKey(artifact));
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<ArtifactKey, PackageInfo> artifactsToPackageInfo;

        Builder() {
            artifactsToPackageInfo = new HashMap<>();
        }

        public Builder addPackage(Artifact artifact, PackageInfo packageInfo) {
            ArtifactKey key = new ArtifactKey(artifact);
            artifactsToPackageInfo.put(key, packageInfo);
            return this;
        }

        public CodeMapper build() {
            return new CodeMapper(artifactsToPackageInfo);
        }

    }

}
