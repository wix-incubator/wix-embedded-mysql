package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.exceptions.MissingDependencyException;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

/**
 * @author viliusl
 * @since 27/09/14
 */
class MysqldExecutable extends Executable<MysqldConfig, MysqldProcess> {

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
            this.initDatabase(config);

        return new MysqldProcess(distribution, config, runtime, this);
    }

    private void markAllLibraryFilesExecutable() {
        for (File f : executable.files(FileType.Library)) {
            f.setExecutable(true);
        }
    }

    private void initDatabase(MysqldConfig config) throws IOException {
        try {
            boolean isSeverVersion57 = config.getVersion().getMajorVersion().equals("5.7");

            Process p = isSeverVersion57 ? getProcessForVersion57() : getProcess();

            int retCode = p.waitFor();

            if (retCode != 0) {
                resolveException(retCode, IOUtils.toString(p.getInputStream()) + IOUtils.toString(p.getErrorStream()));
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Process getProcessForVersion57() throws IOException {
        return Runtime.getRuntime().exec(new String[]{
                        "bin/mysqld",
                        "--no-defaults",
                        "--initialize-insecure",
                        format("--basedir=%s", getBaseDir()),
                        format("--datadir=%s/data", getBaseDir())},
                null,
                getBaseDir());
    }

    private Process getProcess() throws IOException {
        return Runtime.getRuntime().exec(new String[]{
                        "scripts/mysql_install_db",
                        "--no-defaults",
                        format("--basedir=%s", getBaseDir()),
                        format("--datadir=%s/data", getBaseDir())},
                null,
                getBaseDir());
    }

    private void resolveException(int retCode, String output) {
        if (output.contains("error while loading shared libraries: libaio.so")) {
            throw new MissingDependencyException(
                    "System library 'libaio.so.1' missing. " +
                            "Please install it via system package manager, ex. 'sudo apt-get install libaio1'.\n" +
                            "For details see: http://bugs.mysql.com/bug.php?id=60544");
        } else {
            throw new RuntimeException(format("'scripts/mysql_install_db' command exited with error code: '%s' and output: '%s'", retCode, output));
        }
    }

    protected File getBaseDir() {
        return this.executable.baseDir();
    }
}
