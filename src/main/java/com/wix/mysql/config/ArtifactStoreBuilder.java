package com.wix.mysql.config;

import com.wix.mysql.config.directories.TargetGeneratedFixedPath;
import com.wix.mysql.config.extract.NopNaming;


/**
 * @author viliusl
 * @since 27/09/14
 */
public class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

    public ArtifactStoreBuilder defaults() {
        tempDir().setDefault(new TargetGeneratedFixedPath("mysql"));
        executableNaming().setDefault(new NopNaming());
        download().setDefault(new DownloadConfigBuilder().defaults().build());
        return this;
    }
}