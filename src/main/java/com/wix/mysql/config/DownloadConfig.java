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
        private String baseUrl = "https://dev.mysql.com/get/Downloads/";

        /**
         * Download cache location override that by default is set to '~/.embedmysql'.
         *
         * @param downloadCacheDir custom path
         * @return Builder
         */
        public Builder withDownloadCacheDir(String downloadCacheDir) {
            this.downloadCacheDir = downloadCacheDir;
            return this;
        }

        /**
         * base url override that defaults to "https://dev.mysql.com/get/Downloads" where actual mysql binary path must conform to
         * what mysql provides (or otherwise is stored in ~/.embedmysql) - ex. https://dev.mysql.com/get/Downloads/MySQL-5.7/mysql-5.7.18-macos10.12-x86_64.dmg
         *
         * @param baseUrl custom download url
         * @return Builder
         */
        public Builder withBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }


        public DownloadConfig build() {
            return new DownloadConfig(downloadCacheDir, baseUrl);
        }
    }
}
