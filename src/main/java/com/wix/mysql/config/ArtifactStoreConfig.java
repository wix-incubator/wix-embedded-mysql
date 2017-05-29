package com.wix.mysql.config;

public class ArtifactStoreConfig implements AdditionalConfig {

    private final String tempDir;

    ArtifactStoreConfig(final String tempDir) {
        this.tempDir = tempDir;
    }

    public String getTempDir() {
        return tempDir;
    }

    public static Builder anArtifactStoreConfig() {
        return new Builder();
    }

    public static class Builder {
        private String tempDir;

        Builder() {
            this.tempDir = "target/";
        }

        public Builder withTempDir(String tempDir) {
            this.tempDir = tempDir;
            return this;
        }


        public ArtifactStoreConfig build() {
            return new ArtifactStoreConfig(tempDir);
        }
    }
}
