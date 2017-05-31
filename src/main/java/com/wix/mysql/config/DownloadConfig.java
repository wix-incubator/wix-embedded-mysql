package com.wix.mysql.config;

import java.io.File;

public class DownloadConfig implements AdditionalConfig {

    private final String cacheDir;
    private String baseUrl;

    DownloadConfig(final String cacheDir, final String baseUrl) {
        this.cacheDir = cacheDir;
        this.baseUrl = baseUrl;
    }

    /**
     * @deprecated in favour of getCacheDir
     */
    @Deprecated
    public String getDownloadCacheDir() {
        return cacheDir;
    }
    public String getCacheDir() {
        return cacheDir;
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public static Builder aDownloadConfig() {
        return new Builder();
    }

    public static class Builder {
        private String cacheDir = new File(System.getProperty("user.home"), ".embedmysql").getPath();
        private String baseUrl = "https://dev.mysql.com/get/Downloads/";

        /**
         * Download cache location override that by default is set to '~/.embedmysql'.
         *
         * @deprecated in favor of withCacheDir
         *
         * @param downloadCacheDir custom path
         * @return Builder
         */
        @Deprecated
        public Builder withDownloadCacheDir(String downloadCacheDir) {
            this.cacheDir = downloadCacheDir;
            return this;
        }

        /**
         * Download cache location override that by default is set to '~/.embedmysql'.
         *
         * @param cacheDir custom path
         * @return Builder
         */
        public Builder withCacheDir(String cacheDir) {
            this.cacheDir = cacheDir;
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
            return new DownloadConfig(cacheDir, baseUrl);
        }
    }
}
