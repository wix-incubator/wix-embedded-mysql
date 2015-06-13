package com.wix.mysql.config;


import com.wix.mysql.config.directories.TargetGeneratedFixedPath;
import com.wix.mysql.config.extract.NopNaming;
import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.ImmutableExtractedFileSet;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.store.ArtifactStore;
import de.flapdoodle.embed.process.store.Downloader;
import de.flapdoodle.embed.process.store.IArtifactStore;
import de.flapdoodle.embed.process.store.IDownloader;
import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static de.flapdoodle.embed.process.config.store.FileType.Executable;


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
        return new PreExtractingArtifactStore(downloadConfig, tempDir, executableNaming, downloader);
    }

    public static class PreExtractingArtifactStore extends ArtifactStore {

        private IDownloadConfig downloadConfig;
        private IDirectory tempDirFactory;

        public PreExtractingArtifactStore(IDownloadConfig downloadConfig, IDirectory tempDirFactory, ITempNaming executableNaming, IDownloader downloader) {
            super(downloadConfig, tempDirFactory, executableNaming, downloader);
            this.downloadConfig = downloadConfig;
            this.tempDirFactory = tempDirFactory;
        }

        @Override
        public IExtractedFileSet extractFileSet(Distribution distribution) throws IOException {
            File archive = getArtifact(downloadConfig, distribution);
            File sourceFolder = preExtract(archive);
            File destinationFolder = tempDirFactory.asFile();
            ImmutableExtractedFileSet.Builder builder = ImmutableExtractedFileSet.builder(destinationFolder);


            for (FileSet.Entry entry : downloadConfig.getPackageResolver().getFileSet(distribution).entries()) {
                File source = new File(sourceFolder, entry.destination());
                File target = new File(destinationFolder, entry.destination());

                if (entry.matchingPattern().pattern().equals("FOLDER")) {
                    FileUtils.copyDirectory(source, target);
                } else {
                    target.getParentFile().mkdirs();
                    FileUtils.copyFile(source, target);
                    target.setExecutable(true);
                }

                if ((entry.type() == Executable)) {
                    builder.executable(target);
                }
            }

            return builder.build();
        }

        private File preExtract(File downloadedFile) throws IOException {
            File extractedFolder = new File(downloadedFile.getAbsolutePath() + "_extracted");

            if (!extractedFolder.exists()) {
                if (downloadedFile.getAbsolutePath().endsWith(".zip")) {
                    ArchiverFactory.createArchiver(ArchiveFormat.ZIP).extract(downloadedFile, extractedFolder);
                } else {
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP).extract(downloadedFile, extractedFolder);
                }
            }

            File[] folders = extractedFolder.listFiles();

            if (folders.length == 1) {
                return extractedFolder.listFiles()[0];
            } else {
                return extractedFolder;
            }
        }

        private File getArtifact(IDownloadConfig runtime, Distribution distribution) {
            File dir = createOrGetBaseDir(runtime);
            File artifactFile = new File(dir, runtime.getPackageResolver().getPath(distribution));
            return artifactFile.exists() && artifactFile.isFile()?artifactFile:null;
        }

        private File createOrGetBaseDir(IDownloadConfig runtime) {
            File dir = runtime.getArtifactStorePath().asFile();
            createOrCheckDir(dir);
            return dir;
        }

        private void createOrCheckDir(File dir) {
            if(!dir.exists() && !dir.mkdirs()) {
                throw new IllegalArgumentException("Could NOT create Directory " + dir);
            } else if(!dir.isDirectory()) {
                throw new IllegalArgumentException("" + dir + " is not a Directory");
            }
        }

    }
}