package com.wix.mysql.config;

import com.wix.mysql.PackagePaths;
import de.flapdoodle.embed.process.config.store.DownloadPath;
import de.flapdoodle.embed.process.config.store.TimeoutConfigBuilder;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;

import java.net.URI;

public class DownloadConfigBuilder extends de.flapdoodle.embed.process.config.store.DownloadConfigBuilder {

    public DownloadConfigBuilder defaults(
            final DownloadConfig downloadConfig) {
        fileNaming().setDefault(new UUIDTempNaming());
        downloadPath().setDefault(new DownloadPath(downloadConfig.getBaseUrl()));
        progressListener().setDefault(new StandardConsoleProgressListener());
        artifactStorePath().setDefault(new FixedPath(downloadConfig.getDownloadCacheDir()));
        downloadPrefix().setDefault(new DownloadPrefix("embedmysql-download"));
        userAgent().setDefault(new UserAgent("Mozilla/5.0 (compatible; Embedded MySql; +https://github.com/wix/wix-embedded-mysql)"));
        packageResolver().setDefault(new PackagePaths());
        timeoutConfig().setDefault(new TimeoutConfigBuilder().connectionTimeout(10000).readTimeout(60000).build());
        return this;
    }
}
