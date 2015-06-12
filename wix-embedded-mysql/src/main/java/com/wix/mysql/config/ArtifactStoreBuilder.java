package com.wix.mysql.config;

import com.wix.mysql.ExtractingDownloader;
import com.wix.mysql.config.directories.TargetGeneratedFixedPath;
import com.wix.mysql.config.extract.NopNaming;
import de.flapdoodle.embed.process.builder.TypedProperty;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.config.store.ILibraryStore;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.store.*;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.File;
import java.io.IOException;


/**
 * @author viliusl
 * @since 27/09/14
 */
public class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

    private final IDirectory tempDir = new TargetGeneratedFixedPath("mysql");
    private final NopNaming executableNaming = new NopNaming();
    private final IDownloadConfig downloadConfig = new DownloadConfigBuilder().defaults().build();
    private final IDownloader downloader = new Downloader();

    public ArtifactStoreBuilder defaults() {
        tempDir().setDefault(tempDir);
        executableNaming().setDefault(executableNaming);
        download().setDefault(downloadConfig);
        downloader().setDefault(downloader);
        /**
         * Disabled caching artifact store, as our use case is to start mysqld before all tests and shutdown afterwards;
         * Caching artifact has some magic, where it has clean-up threads/shutdown hooks and in some cases
         * files are removed before proper mysqld shutdown kicks and and guess what - mysqladmin is gone so there is
         * no way to shutdown process properly.
         */
        useCache(false);
        return this;
    }

    @Override
    public IArtifactStore build() {
        Object artifactStore = new MyArtifactStore(downloadConfig, tempDir, executableNaming, downloader);
        return (IArtifactStore)artifactStore;

    }

    public static class MyArtifactStore extends ArtifactStore {

        private IDownloadConfig downloadConfig;

        public MyArtifactStore(IDownloadConfig downloadConfig, IDirectory tempDirFactory, ITempNaming executableNaming, IDownloader downloader) {
            super(downloadConfig, tempDirFactory, executableNaming, downloader);
            this.downloadConfig = downloadConfig;
        }

        @Override
        public boolean checkDistribution(Distribution distribution) throws IOException {
            return super.checkDistribution(distribution);
        }

        @Override
        public IExtractedFileSet extractFileSet(Distribution distribution) throws IOException {
            File archive = getArtifact(downloadConfig, distribution);
            File extractedFiles = preExtract(archive);
            return super.extractFileSet(distribution);
        }

        private File preExtract(File downloadedFile) throws IOException {
            File outputFolder = new File(downloadedFile.getParentFile(), "." + downloadedFile.getName());

            if (downloadedFile.getAbsolutePath().endsWith(".zip")) {
                ArchiverFactory.createArchiver(ArchiveFormat.ZIP).extract(downloadedFile, outputFolder);
            } else {
                ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP).extract(downloadedFile, outputFolder);
            }

            return downloadedFile;

        }

        private static File getArtifact(IDownloadConfig runtime, Distribution distribution) {
            File dir = createOrGetBaseDir(runtime);
            File artifactFile = new File(dir, runtime.getPackageResolver().getPath(distribution));
            return artifactFile.exists() && artifactFile.isFile()?artifactFile:null;
        }

        private static File createOrGetBaseDir(IDownloadConfig runtime) {
            File dir = runtime.getArtifactStorePath().asFile();
            createOrCheckDir(dir);
            return dir;
        }

        private static void createOrCheckDir(File dir) {
            if(!dir.exists() && !dir.mkdirs()) {
                throw new IllegalArgumentException("Could NOT create Directory " + dir);
            } else if(!dir.isDirectory()) {
                throw new IllegalArgumentException("" + dir + " is not a Directory");
            }
        }

    }
}