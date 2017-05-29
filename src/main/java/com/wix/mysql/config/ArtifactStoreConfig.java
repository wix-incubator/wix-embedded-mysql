package com.wix.mysql.config;

import java.io.File;

public class ArtifactStoreConfig implements AdditionalConfig {

    private final String tempDir;
    private final String downloadCacheDir;

    ArtifactStoreConfig(
            final String tempDir,
            final String downloadCacheDir) {
        this.tempDir = tempDir;
        this.downloadCacheDir = downloadCacheDir;
    }

    public String getTempDir() {
        return tempDir;
    }

    public String getDownloadCacheDir() {
        return downloadCacheDir;
    }

    public static Builder anArtifactStoreConfig() {
        return new Builder();
    }

    public static class Builder {
        private String tempDir;
        private String downloadCacheDir;


        Builder() {
            this.tempDir = "target/";
            this.downloadCacheDir = new File(System.getProperty("user.home"), ".embedmysql").getPath();
        }

        public Builder withTempDir(String tempDir) {
            this.tempDir = tempDir;
            return this;
        }

        public Builder withDownloadCacheDir(String downloadCacheDir) {
            this.downloadCacheDir = downloadCacheDir;
            return this;
        }

        public ArtifactStoreConfig build() {
            return new ArtifactStoreConfig(tempDir, downloadCacheDir);
        }
    }
}
