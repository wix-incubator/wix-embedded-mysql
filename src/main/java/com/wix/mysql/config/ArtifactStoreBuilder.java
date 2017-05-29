package com.wix.mysql.config;

import com.wix.mysql.config.directories.TargetGeneratedFixedPath;
import com.wix.mysql.config.extract.NoopNaming;
import com.wix.mysql.config.extract.PathPrefixingNaming;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.store.Downloader;

public class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ExtractedArtifactStoreBuilder {

    public ArtifactStoreBuilder defaults(String directoryName) {
        ArtifactStoreConfig storeConf = ArtifactStoreConfig.anArtifactStoreConfig().build();

        tempDir().setDefault(new TargetGeneratedFixedPath(storeConf.getTempDir()));
        executableNaming().setDefault(new PathPrefixingNaming("bin/"));
        download().setDefault(new DownloadConfigBuilder().defaults().build());
        downloader().setDefault(new Downloader());
        extractDir().setDefault(new UserHome(".embedmysql/extracted"));
        extractExecutableNaming().setDefault(new NoopNaming());

        tempDir().setDefault(new TargetGeneratedFixedPath(directoryName));

        return this;
    }
}