package com.atlassian.maven.whence.data;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Packages;
import org.apache.maven.artifact.Artifact;

public class PackageInfo {

    private final Artifact artifact;
    private final Parameters imports;
    private final Parameters exports;
    private final Packages contains;
    private final Packages references;

    private PackageInfo(Artifact artifact, Parameters imports, Parameters exports, Packages contains, Packages references) {
        this.artifact = artifact;
        this.imports = imports;
        this.exports = exports;
        this.contains = contains;
        this.references = references;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public Packages getContains() {
        return contains;
    }

    public Parameters getExports() {
        return exports;
    }

    public Parameters getImports() {
        return imports;
    }

    public Packages getReferences() {
        return references;
    }

    public static Builder builder(Artifact artifact) {
        return new Builder(artifact);
    }

    public static class Builder {
        final Artifact artifact;
        Parameters imports = new Parameters();
        Parameters exports = new Parameters();
        Packages contains = new Packages();
        Packages references = new Packages();

        Builder(Artifact artifact) {
            this.artifact = artifact;
        }

        public Builder setImports(Parameters packages) {
            this.imports = packages;
            return this;
        }

        public Builder setExports(Parameters packages) {
            this.exports = packages;
            return this;
        }

        public Builder setContains(Packages packages) {
            this.contains = packages;
            return this;
        }

        public Builder setReferences(Packages packages) {
            this.references = packages;
            return this;
        }


        public PackageInfo build() {
            return new PackageInfo(artifact, imports, exports, contains, references);
        }
    }
}
