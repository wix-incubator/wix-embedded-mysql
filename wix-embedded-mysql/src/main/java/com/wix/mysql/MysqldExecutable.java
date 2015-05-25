package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.exceptions.MissingDependencyException;
import com.wix.mysql.input.CollectingLogOutputProcessor;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.runtime.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldExecutable extends Executable<MysqldConfig, MysqldProcess> {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private final IExtractedFileSet executable;
    private final IRuntimeConfig runtimeConfig;

    public MysqldExecutable(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final IExtractedFileSet executable) {
        super(distribution, config, runtimeConfig, executable);
        this.executable = executable;
        this.runtimeConfig = runtimeConfig;
    }

    @Override
    protected MysqldProcess start(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtime) throws IOException {

        markAllLibraryFilesExecutable();

        if (Platform.detect().isUnixLike())// windows already comes with data - otherwise installed python is needed:/
            this.initDatabase();

        return new MysqldProcess(distribution, config, runtime, this);
    }

    private void markAllLibraryFilesExecutable() {
        for (File f: executable.files(FileType.Library)) {
            f.setExecutable(true);
        }
    }

    private void initDatabase() throws IOException {
        try {
            String baseDir = this.executable.generatedBaseDir().getAbsolutePath();
            CollectingLogOutputProcessor logTo = new CollectingLogOutputProcessor(log);

            Process p = Runtime.getRuntime().exec(new String[]{
                            "scripts/mysql_install_db",
                            "--no-defaults",
                            String.format("--basedir=%s", baseDir),
                            String.format("--datadir=%s/data", baseDir)},
                    null,
                    this.executable.generatedBaseDir());

            Processors.connect(new InputStreamReader(p.getInputStream()), logTo);
            Processors.connect(new InputStreamReader(p.getErrorStream()), logTo);

            int retCode = p.waitFor();

            if (retCode != 0) {
                resolveException(retCode, logTo.getOuptut());
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void resolveException(int retCode, String output) {
        if (output.contains("error while loading shared libraries: libaio.so")) {
            throw new MissingDependencyException(
                    "System library 'libaio.so.1' missing. " +
                    "Please install it via system package manager, ex. 'sudo apt-get install libaio1'.\n" +
                    "For details see: http://bugs.mysql.com/bug.php?id=60544");
        } else {
            throw new RuntimeException(String.format("'scripts/mysql_install_db' command exited with error code: %s", retCode));
        }
    }

    protected File getBaseDir() { return this.executable.generatedBaseDir(); }
    protected IRuntimeConfig getRuntimeConfig() { return this.runtimeConfig; }
}