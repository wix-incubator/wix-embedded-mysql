package com.wix.mysql.config;

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.extract.IArchiveEntry;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.mapper.DestinationEntry;
import de.flapdoodle.embed.process.extract.mapper.IDestinationFileMapper;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.file.Files;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

    public ArtifactStoreBuilder defaults() {
        destinationFileProducer(new DestinationFileProducer());
        tempDir().setDefault(new TempDirectory());
        executableNaming().setDefault(new OriginalNaming());
        download().setDefault(new DownloadConfigBuilder().defaults().build());
        return this;
    }

    //put everything in temp folder
    public static class TempDirectory implements IDirectory {

        private File baseTempDir = new File("target").getAbsoluteFile();

        @Override
        public File asFile() {
            if (!baseTempDir.exists() || !baseTempDir.isDirectory()) {
                throw new RuntimeException("Could not locate target build dir. \n" +
                        "Have you set your working directory to the $MODULE_DIR$?");
            }

            try {
                File mysql = Files.createTempDir(baseTempDir, "mysql");
                return mysql.getAbsoluteFile();
            } catch (IOException e) {
                throw new RuntimeException("unable to create mysql temp directory.", e);
            }
        }

        @Override
        public boolean isGenerated() { return true; }
    }

    //do not rename artifacts
    public static class OriginalNaming implements ITempNaming {

        @Override
        public String nameFor(String prefix, String postfix) { return postfix; }
    }

    //extract files by stripping mysql-xx root archive folder.
    public static class DestinationFileProducer implements IDestinationFileMapper {
        @Override
        public DestinationEntry fromSource(IArchiveEntry archiveEntry, FileSet.Entry fileSetEntry) {
            String[] res = archiveEntry.getName().split("/");
            String normalized = StringUtils.join(Arrays.copyOfRange(res, 1, res.length), "/");
            return new DestinationEntry(fileSetEntry.type(), normalized, true);
        }
    }

}