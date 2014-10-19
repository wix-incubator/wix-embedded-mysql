package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.runtime.Executable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.flapdoodle.embed.process.io.Processors.logTo;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldExecutable extends Executable<MysqldConfig, MysqldProcess> {

    private final Logger log = Logger.getLogger(getClass().getName());

    private final IExtractedFileSet executable;

    public MysqldExecutable(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final IExtractedFileSet executable) {
        super(distribution, config, runtimeConfig, executable);
        this.executable = executable;
    }

    @Override
    protected MysqldProcess start(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtime) throws IOException {

        markAllLibraryFilesExecutable();

        if (Platform.detect().isUnixLike())// windows already comes with data - otherwise installed python is needed:/
            this.initDatabase();

        return new MysqldProcess(distribution, config, runtime, this, config.getTimeout());
    }

    private void markAllLibraryFilesExecutable() {
        for (File f: executable.files(FileType.Library)) {
            f.setExecutable(true);
        }
    }

    private void initDatabase() throws IOException {
        try {
            // this probably can be written better than what it is now.
            Process p = Runtime.getRuntime().exec(new String[]{
                            "scripts/mysql_install_db",
                            "--force", // do not lookup dns - no need for resolveip command to be present
                            "--no-defaults",
                            "--explicit_defaults_for_timestamp"}, // do not read defaults file.
                    null,
                    this.executable.generatedBaseDir());

            Processors.connect(new InputStreamReader(p.getInputStream()), logTo(log, Level.FINER));
            Processors.connect(new InputStreamReader(p.getErrorStream()), logTo(log, Level.FINER));

            int retCode = p.waitFor();

            if (retCode != 0) {
                throw new RuntimeException(String.format("'scripts/mysql_install_db' command exited with error code: %s", retCode));
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}