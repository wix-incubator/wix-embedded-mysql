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
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static de.flapdoodle.embed.process.config.store.FileType.Executable;
import static de.flapdoodle.embed.process.config.store.FileType.Library;


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
        return new MyArtifactStore(downloadConfig, tempDir, executableNaming, downloader);
    }

    public static class MyArtifactStore extends ArtifactStore {

        private IDownloadConfig downloadConfig;
        private IDirectory tempDirFactory;

        public MyArtifactStore(IDownloadConfig downloadConfig, IDirectory tempDirFactory, ITempNaming executableNaming, IDownloader downloader) {
            super(downloadConfig, tempDirFactory, executableNaming, downloader);
            this.downloadConfig = downloadConfig;
            this.tempDirFactory = tempDirFactory;
        }

        //TODO: move to proper place, here just to prove the point
        FileSet filesToExtract = FileSet.builder()
        .addEntry(Executable, "bin/mysqld")
        .addEntry(Library,    "bin/mysql")
        .addEntry(Library,    "bin/resolveip")
        .addEntry(Library,    "bin/mysqladmin")
        .addEntry(Library,    "bin/my_print_defaults")
        .addEntry(Library,    "scripts/mysql_install_db")
        .addEntry(Library,    "lib/plugin/innodb_engine.so")
        //.addEntry(Library,    "lib/plugin/auth_socket.so")//only linux
        .addEntry(Library,    "share/english/errmsg.sys")
        .addEntry(Library,    "share/fill_help_tables.sql")
        .addEntry(Library,    "share/mysql_security_commands.sql")
        .addEntry(Library,    "share/mysql_system_tables.sql")
        .addEntry(Library,    "share/mysql_system_tables_data.sql")
        .addEntry(Library,    "support-files/my-default.cnf")
        .build();


        @Override
        public IExtractedFileSet extractFileSet(Distribution distribution) throws IOException {
            File archive = getArtifact(downloadConfig, distribution);
            File sourceFolder = preExtract(archive);
            File destinationFolder = tempDirFactory.asFile();
            destinationFolder.mkdir();
            ImmutableExtractedFileSet.Builder builder = ImmutableExtractedFileSet.builder(destinationFolder);

            for (FileSet.Entry entry : filesToExtract.entries()) {
                File source = new File(sourceFolder, entry.destination());
                File target = new File(destinationFolder, entry.destination());
                Files.copy(source.toPath(), target.toPath());
                target.setExecutable(true);
                if (entry.type() == Executable) {
                    builder.executable(target);
                } else {
                    builder.file(entry.type(), target);
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

            return extractedFolder.listFiles()[0];
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