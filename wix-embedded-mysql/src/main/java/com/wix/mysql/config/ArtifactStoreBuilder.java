package com.wix.mysql.config;

import com.wix.mysql.config.directories.TargetGeneratedFixedPath;
import com.wix.mysql.config.extract.NopNaming;
import de.flapdoodle.embed.process.store.Downloader;


/**
 * @author viliusl
 * @since 27/09/14
 */
public class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

    public ArtifactStoreBuilder defaults() {
        tempDir().setDefault(new TargetGeneratedFixedPath("mysql"));
        executableNaming().setDefault(new NopNaming());
        download().setDefault(new DownloadConfigBuilder().defaults().build());
        downloader().setDefault(new Downloader());
        /**
         * Disabled caching artifact store, as our use case is to start mysqld before all tests and shutdown afterwards;
         * Caching artifact has some magic, where it has clean-up threads/shutdown hooks and in some cases
         * files are removed before proper mysqld shutdown kicks and and guess what - mysqladmin is gone so there is
         * no way to shutdown process properly.
         */
        useCache(false);
        return this;
    }
}