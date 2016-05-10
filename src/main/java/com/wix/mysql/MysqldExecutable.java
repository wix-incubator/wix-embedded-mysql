package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Initializer;
import com.wix.mysql.distribution.Version;
import com.wix.mysql.exceptions.MissingDependencyException;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * @author viliusl
 * @since 27/09/14
 */
class MysqldExecutable extends Executable<MysqldConfig, MysqldProcess> {

    private final IExtractedFileSet executable;

    private static class Mysql57Initializer implements Initializer {
        @Override
        public boolean matches(Version version) {
            return version.getMajorVersion().equals("5.7");
        }

        @Override
        public void apply(IExtractedFileSet files) throws IOException {
            File baseDir = files.baseDir();
            try {
                FileUtils.deleteDirectory(new File(baseDir, "data"));
                Process p = Runtime.getRuntime().exec(new String[]{
                                files.executable().getAbsolutePath(),
                                "--no-defaults",
                                "--initialize-insecure",
                                format("--basedir=%s", baseDir),
                                format("--datadir=%s/data", baseDir)},
                        null,
                        baseDir);


                int retCode = p.waitFor();

                if (retCode != 0) {
                    resolveException(retCode, IOUtils.toString(p.getInputStream()) + IOUtils.toString(p.getErrorStream()));
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
                throw new RuntimeException(format("Command exited with error code: '%s' and output: '%s'", retCode, output));
            }
        }
    }

    private static class NixBefore57Initializer implements Initializer {
        @Override
        public boolean matches(Version version) {
            return Platform.detect().isUnixLike() &&
                    (version.getMajorVersion().equals("5.6") || version.getMajorVersion().equals("5.5"));
        }

        @Override
        public void apply(IExtractedFileSet files) throws IOException {
            File baseDir = files.baseDir();
            try {
                Process p = Runtime.getRuntime().exec(new String[]{
                                "scripts/mysql_install_db",
                                "--no-defaults",
                                format("--basedir=%s", baseDir),
                                format("--datadir=%s/data", baseDir)},
                        null,
                        baseDir);

                int retCode = p.waitFor();

                if (retCode != 0) {
                    resolveException(retCode, IOUtils.toString(p.getInputStream()) + IOUtils.toString(p.getErrorStream()));
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
                throw new RuntimeException(format("Command exited with error code: '%s' and output: '%s'", retCode, output));
            }
        }

    }

    private List<Initializer> initializers = new ArrayList<>();

    public MysqldExecutable(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final IExtractedFileSet executable) {
        super(distribution, config, runtimeConfig, executable);
        this.executable = executable;
        initializers.add(new Mysql57Initializer());
        initializers.add(new NixBefore57Initializer());
    }


    @Override
    protected MysqldProcess start(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtime) throws IOException {

        markAllLibraryFilesExecutable();

        for(Initializer initializer : initializers) {
            if(initializer.matches(config.getVersion())) {
                initializer.apply(executable);
            }
        }

        return new MysqldProcess(distribution, config, runtime, this);
    }

    private void markAllLibraryFilesExecutable() {
        for (File f : executable.files(FileType.Library)) {
            f.setExecutable(true);
        }
    }

    public File getBaseDir() {
        return executable.baseDir();
    }
}
