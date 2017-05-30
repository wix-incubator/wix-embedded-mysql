package com.wix.mysql.config;

import java.io.File;

public class DownloadConfig implements AdditionalConfig {

    private final String downloadCacheDir;
    private String baseUrl;

    DownloadConfig(
            final String downloadCacheDir,
            final String baseUrl) {

        this.downloadCacheDir = downloadCacheDir;
        this.baseUrl = baseUrl;
    }

    public String getDownloadCacheDir() {
        return downloadCacheDir;
    }
    public String getBaseUrl() {
        return baseUrl;
    }

    public static Builder aDownloadConfig() {
        return new Builder();
    }

    public static class Builder {
        private String downloadCacheDir = new File(System.getProperty("user.home"), ".embedmysql").getPath();
        private String baseUrl = "https://dev.mysql.com";

        public Builder withDownloadCacheDir(String downloadCacheDir) {
            this.downloadCacheDir = downloadCacheDir;
            return this;
        }

        public Builder withBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }


        public DownloadConfig build() {
            return new DownloadConfig(downloadCacheDir, baseUrl);
        }
    }
}
