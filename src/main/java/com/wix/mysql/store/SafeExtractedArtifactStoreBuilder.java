package com.wix.mysql.store;

import com.wix.mysql.config.DownloadConfig;
import com.wix.mysql.config.DownloadConfigBuilder;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.directories.TargetGeneratedFixedPath;
import com.wix.mysql.config.extract.NoopNaming;
import com.wix.mysql.config.extract.PathPrefixingNaming;
import de.flapdoodle.embed.process.extract.DirectoryAndExecutableNaming;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.store.Downloader;
import de.flapdoodle.embed.process.store.IArtifactStore;

import java.io.File;
import java.util.UUID;

public class SafeExtractedArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ExtractedArtifactStoreBuilder {

    public SafeExtractedArtifactStoreBuilder defaults(
            final MysqldConfig mysqldConfig,
            final DownloadConfig downloadConfig) {

        String tempExtractDir = String.format("mysql-%s-%s", mysqldConfig.getVersion().getMajorVersion(), UUID.randomUUID());
        String combinedPath = new File(mysqldConfig.getTempDir(), tempExtractDir).getPath();
        IDirectory preExtractDir = new FixedPath(new File(downloadConfig.getCacheDir(), "extracted").getPath());

        executableNaming().setDefault(new PathPrefixingNaming("bin/"));
        download().setDefault(new DownloadConfigBuilder().defaults(downloadConfig).build());
        downloader().setDefault(new Downloader());
        extractDir().setDefault(preExtractDir);
        extractExecutableNaming().setDefault(new NoopNaming());

        tempDir().setDefault(new TargetGeneratedFixedPath(combinedPath));

        return this;
    }

    @Override
    public IArtifactStore build() {
        DirectoryAndExecutableNaming extract = new DirectoryAndExecutableNaming(get(EXTRACT_DIR_FACTORY), get(EXTRACT_EXECUTABLE_NAMING));
        DirectoryAndExecutableNaming temp = new DirectoryAndExecutableNaming(tempDir().get(), executableNaming().get());

        return new SafeExtractedArtifactStore(get(DOWNLOAD_CONFIG), get(DOWNLOADER), extract, temp);
    }
}
