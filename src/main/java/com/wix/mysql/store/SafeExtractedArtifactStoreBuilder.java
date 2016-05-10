package com.wix.mysql.store;

import com.wix.mysql.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.process.extract.DirectoryAndExecutableNaming;
import de.flapdoodle.embed.process.store.IArtifactStore;

/**
 * @author maximn
 * @since 22-Oct-2015
 */
public class SafeExtractedArtifactStoreBuilder extends ArtifactStoreBuilder {
    @Override
    public IArtifactStore build() {
        DirectoryAndExecutableNaming extract = new DirectoryAndExecutableNaming(get(EXTRACT_DIR_FACTORY), get(EXTRACT_EXECUTABLE_NAMING));
        DirectoryAndExecutableNaming temp = new DirectoryAndExecutableNaming(tempDir().get(), executableNaming().get());

        return new SafeExtractedArtifactStore(get(DOWNLOAD_CONFIG), get(DOWNLOADER), extract, temp);
    }
}
