package com.wix.mysql.config;

import com.wix.mysql.PackagePaths;
import de.flapdoodle.embed.process.config.store.DownloadPath;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.ConsoleOneLineProgressListener;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class DownloadConfigBuilder extends de.flapdoodle.embed.process.config.store.DownloadConfigBuilder {

    public DownloadConfigBuilder defaults() {
        fileNaming().setDefault(new UUIDTempNaming());
        downloadPath().setDefault(new DownloadPath("http://dev.mysql.com/get/Downloads/"));
        progressListener().setDefault(new ConsoleOneLineProgressListener());
        artifactStorePath().setDefault(new UserHome(".embedmysql"));
        downloadPrefix().setDefault(new DownloadPrefix("embedmysql-download"));
        userAgent().setDefault(new UserAgent("Mozilla/5.0 (compatible; Embedded MySql; +https://github.com/wix/wix-embedded-mysql)"));
        packageResolver().setDefault(new PackagePaths());
        return this;
    }
}
