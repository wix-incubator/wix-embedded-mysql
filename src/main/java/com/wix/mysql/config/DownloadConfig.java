package com.wix.mysql.config;

import java.io.File;

public class DownloadConfig implements AdditionalConfig {

    private final String downloadCacheDir;

    DownloadConfig(
            final String downloadCacheDir) {
        this.downloadCacheDir = downloadCacheDir;
    }

    public String getDownloadCacheDir() {
        return downloadCacheDir;
    }

    public static Builder aDownloadConfig() {
        return new Builder();
    }

    public static class Builder {
        private String downloadCacheDir;


        Builder() {
            this.downloadCacheDir = new File(System.getProperty("user.home"), ".embedmysql").getPath();
        }

        public Builder withDownloadCacheDir(String downloadCacheDir) {
            this.downloadCacheDir = downloadCacheDir;
            return this;
        }

        public DownloadConfig build() {
            return new DownloadConfig(downloadCacheDir);
        }
    }
}
