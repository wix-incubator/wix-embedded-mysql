package com.wix.mysql.config;

import com.wix.mysql.config.directories.TargetGeneratedFixedPath;
import com.wix.mysql.config.extract.NopNaming;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.store.Downloader;


/**
 * @author viliusl
 * @since 27/09/14
 */
public class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ExtractedArtifactStoreBuilder {

    public ArtifactStoreBuilder defaults() {
        tempDir().setDefault(new TargetGeneratedFixedPath("mysql"));
        executableNaming().setDefault(new NopNaming("bin/"));
        download().setDefault(new DownloadConfigBuilder().defaults().build());
        downloader().setDefault(new Downloader());
        extractDir().setDefault(new UserHome(".embedmysql/extracted"));
        extractExecutableNaming().setDefault(new NopNaming());

        return this;
    }
}