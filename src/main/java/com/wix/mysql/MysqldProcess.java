package com.wix.mysql;

import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.input.LogFileProcessor;
import com.wix.mysql.input.OutputWatchStreamProcessor;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.LogWatchStreamProcessor;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldProcess extends AbstractProcess<MysqldConfig, MysqldExecutable, MysqldProcess> {

    private final static Logger logger = LoggerFactory.getLogger(MysqldProcess.class);

    private IRuntimeConfig unsafeRuntimeConfig;
    private OutputWatchStreamProcessor logWatch = null;
    private LogFileProcessor logFile = null;

    public MysqldProcess(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final MysqldExecutable executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
        this.unsafeRuntimeConfig = runtimeConfig;
    }

    @Override
    protected void onBeforeProcessStart(ProcessBuilder processBuilder, MysqldConfig config, IRuntimeConfig runtimeConfig) {
        super.onBeforeProcessStart(processBuilder, config, runtimeConfig);

        logWatch = new OutputWatchStreamProcessor(
                Sets.newHashSet("ready for connections"),
                Sets.newHashSet("[ERROR]"),
                StreamToLineProcessor.wrap(runtimeConfig.getProcessOutput().getOutput()));

        logFile = new LogFileProcessor(
                new File(this.getExecutable().executable.generatedBaseDir() + "/data/error.log"),
                logWatch);
    }

    @Override
    public void onAfterProcessStart(final ProcessControl process, final IRuntimeConfig runtimeConfig) throws IOException {
        try {

            logWatch.waitForResult(getConfig().getTimeout());

            if (!logWatch.isInitWithSuccess()) {
                throw new RuntimeException("mysql start failed with error: " + logWatch.getFailureFound());
            }

            new MysqlConfigurer(getConfig()).configure();
        } catch (Exception e) {
            // emit IO exception for {@link AbstractProcess} would try to stop running process gracefully
            throw new IOException(e);
        } finally {
            if (logFile != null) logFile.shutdown();
        }
    }


    @Override
    protected List<String> getCommandLine(Distribution distribution, MysqldConfig config, IExtractedFileSet exe) throws IOException {
        final String baseDir = exe.generatedBaseDir().getAbsolutePath();

        //TODO: create factory for platform-specific commands;
        if (Platform.detect() == Platform.Windows) {
            return Collections.newArrayList(
                    exe.executable().getAbsolutePath(),
                    "--no-defaults",
                    "--log-output=NONE",
                    "--enable-named-pipe",
                    String.format("--basedir=%s", baseDir),
                    String.format("--datadir=%s/data", baseDir),
                    String.format("--plugin-dir=%s/lib/plugin", baseDir),
                    String.format("--pid-file=%s.pid", pidFile(exe.executable())),
                    String.format("--lc-messages-dir=%s/share", baseDir),
                    String.format("--socket=%s", sockFile(exe)),
                    String.format("--port=%s", config.getPort()),
                    String.format("--log-error=%s/data/error.log", baseDir));
        } else {
            return Collections.newArrayList(
                    exe.executable().getAbsolutePath(),
                    "--no-defaults",
                    "--log-output=NONE",
                    String.format("--basedir=%s", baseDir),
                    String.format("--datadir=%s/data", baseDir),
                    String.format("--plugin-dir=%s/lib/plugin", baseDir),
                    String.format("--pid-file=%s.pid", pidFile(exe.executable())),
                    String.format("--lc-messages-dir=%s/share", baseDir),
                    String.format("--socket=%s", sockFile(exe)),
                    String.format("--port=%s", config.getPort()),
                    String.format("--log-error=%s/data/error.log", baseDir));
        }
    }

    @Override
    protected void stopInternal() {
        synchronized (this) {
            logger.info("try to stop mysqld");
            if (!stopUsingMysqldadmin()) {
                logger.warn("could not stop mysqld via mysqladmin, try next");
                if (!sendKillToProcess()) {
                    logger.warn("could not stop mysqld, try next");
                    if (!sendTermToProcess()) {
                        logger.warn("could not stop mysqld, try next");
                        if (!tryKillToProcess()) {
                            logger.warn("could not stop mysqld the second time, try one last thing");
                            try {
                                stopProcess();
                            } catch (IllegalStateException e) {
                                logger.error("error while trying to stop mysql process", e);
                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    protected void cleanupInternal() {}

    private boolean stopUsingMysqldadmin() {
        boolean retValue = false;

        Reader stdOut = null;
        Reader stdErr = null;
        LogFileProcessor processor = null;
        Set<String> successPatterns = Sets.newHashSet("'Can't connect to MySQL server on 'localhost'");

        try {
            Process p;

            if (Platform.detect() == Platform.Windows) {
                String cmd = Paths.get(getExecutable().getFile().generatedBaseDir().getAbsolutePath(), "bin", "mysqladmin.exe").toString();
                successPatterns.add("mysqld.exe: Shutdown complete");

                p = Runtime.getRuntime().exec(new String[] {
                        cmd, "--no-defaults", "--protocol=tcp",
                        String.format("-u%s", MysqldConfig.SystemDefaults.USERNAME),
                        "shutdown"});
            } else {
                String cmd = Paths.get(getExecutable().getFile().generatedBaseDir().getAbsolutePath(), "bin", "mysqladmin").toString();
                successPatterns.add("mysqld: Shutdown complete");

                p = Runtime.getRuntime().exec(new String[] {
                        cmd, "--no-defaults", "--protocol=tcp",
                        String.format("-u%s", MysqldConfig.SystemDefaults.USERNAME),
                        //String.format("--socket=%s", sockFile(getExecutable().executable)),
                        "shutdown"});
            }

            retValue = p.waitFor() == 0;

            OutputWatchStreamProcessor outputWatch = new OutputWatchStreamProcessor(
                    successPatterns,
                    Sets.newHashSet("[ERROR]"),
                    StreamToLineProcessor.wrap(unsafeRuntimeConfig.getProcessOutput().getOutput()));

            processor = new LogFileProcessor(new File(this.getExecutable().executable.generatedBaseDir() + "/data/error.log"), outputWatch);

            stdOut = new InputStreamReader(p.getInputStream());
            stdErr = new InputStreamReader(p.getErrorStream());

            if (retValue) {
                outputWatch.waitForResult(getConfig().getTimeout());

                if (!outputWatch.isInitWithSuccess()) {
                    logger.error("mysql shutdown failed. Expected to find in output: 'Shutdown complete', got: " + outputWatch.getFailureFound());
                    retValue = false;
                }
            } else {
                logger.error("mysql shutdown failed with error code: " + p.waitFor() + " and message: " + CharStreams.toString(stdErr));
            }

        } catch (InterruptedException e) {
            logger.warn("Encountered error why shutting down process.", e);
        } catch (IOException e) {
            logger.warn("Encountered error why shutting down process.", e);
        } finally {
            closeCloseable(stdOut);
            closeCloseable(stdErr);
            if (processor != null) processor.shutdown();
        }

        return retValue;
    }

    /**
     * Work-around to get Executable in hooks where it's not provided and as
     * all init is done in base class constructor, local vars are still not
     * initialized:/
     */
    private MysqldExecutable getExecutable() {
        try {
            Field f = AbstractProcess.class.getDeclaredField("executable");
            f.setAccessible(true);
            return (MysqldExecutable)f.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper for getting stable sock file. Saving to local instance variable on service start does not work due
     * to the way flapdoodle process library works - it does all init in {@link AbstractProcess} and instance of
     * {@link MysqldProcess} is not yet present, so vars are not initialized.
     * This algo gives stable sock file based on single run profile, but can leave trash sock files in tmp dir.
     *
     * Notes:
     * .sock file needs to be in system temp dir and not in ex. target/...
     * This is due to possible problems with existing mysql installation and apparmor profiles
     * in linuxes.
     */
    private String sockFile(IExtractedFileSet exe) throws IOException {
        String sysTempDir = System.getProperty("java.io.tmpdir");
        String sockFile = String.format("%s.sock", exe.generatedBaseDir().getName());
        return new File(sysTempDir, sockFile).getAbsolutePath();
    }

    //Cannot use higher version of guava than 16 right now due to framework (wix-embedded-mysql) uses this
    //TODO: make sure 18 version is in FW
    private static void closeCloseable(Reader reader) {
        try {
            if (reader != null) reader.close();
        } catch (IOException e) {

        }

    }
}


