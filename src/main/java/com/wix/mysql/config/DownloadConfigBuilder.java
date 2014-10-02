package com.wix.mysql.config;

import com.wix.mysql.PackagePaths;
import de.flapdoodle.embed.process.config.store.IDownloadPath;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class DownloadConfigBuilder extends de.flapdoodle.embed.process.config.store.DownloadConfigBuilder {

    public DownloadConfigBuilder defaults() {
        fileNaming().setDefault(new UUIDTempNaming());
        downloadPath().setDefault(new DownloadPath());
        progressListener().setDefault(new StandardConsoleProgressListener());
        artifactStorePath().setDefault(new UserHome(".embedmysql"));
        downloadPrefix().setDefault(new DownloadPrefix("embedmysql-download"));
        userAgent().setDefault(new UserAgent("Mozilla/5.0 (compatible; Embedded MySql; +https://github.com/wix/wix-embedded-mysql)"));
        packageResolver().setDefault(new PackagePaths());
        return this;
    }

    public static class DownloadPath implements IDownloadPath {
        @Override
        public String getPath(Distribution distribution) {
            return "http://dev.mysql.com/get/Downloads/";

        }
    }

}
