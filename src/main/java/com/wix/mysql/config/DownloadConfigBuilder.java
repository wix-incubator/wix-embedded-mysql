package com.wix.mysql.config;

import com.wix.mysql.PackagePaths;
import de.flapdoodle.embed.process.config.store.DownloadPath;
import de.flapdoodle.embed.process.config.store.TimeoutConfigBuilder;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import org.apache.commons.lang3.StringUtils;

public class DownloadConfigBuilder extends
    de.flapdoodle.embed.process.config.store.DownloadConfigBuilder {


    public DownloadConfigBuilder defaults() {
        String artifactStoreProp = System.getProperty("mysql.artifact.store");
        IDirectory artifactStore =
            StringUtils.isEmpty(artifactStoreProp) ? new UserHome(".embedmysql")
                : new FixedPath(artifactStoreProp);
        fileNaming().setDefault(new UUIDTempNaming());
        downloadPath().setDefault(new DownloadPath("https://dev.mysql.com/get/Downloads/"));
        progressListener().setDefault(new StandardConsoleProgressListener());
        artifactStorePath().setDefault(artifactStore);
        downloadPrefix().setDefault(new DownloadPrefix("embedmysql-download"));
        userAgent().setDefault(new UserAgent(
            "Mozilla/5.0 (compatible; Embedded MySql; +https://github.com/wix/wix-embedded-mysql)"));
        packageResolver().setDefault(new PackagePaths());
        timeoutConfig().setDefault(
            new TimeoutConfigBuilder().connectionTimeout(10000).readTimeout(60000).build());
        return this;
    }
}
