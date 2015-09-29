package com.wix.mysql;

import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.input.LogFileProcessor;
import com.wix.mysql.input.OutputWatchStreamProcessor;
import com.wix.mysql.utils.Utils;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
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

import static com.wix.mysql.utils.Utils.closeCloseables;
import static de.flapdoodle.embed.process.distribution.Platform.Windows;
import static java.lang.String.format;

/**
 * @author viliusl
 * @since 27/09/14
 */
class MysqldProcess extends AbstractProcess<MysqldConfig, MysqldExecutable, MysqldProcess> {

    private final static Logger logger = LoggerFactory.getLogger(MysqldProcess.class);

    private OutputWatchStreamProcessor logWatch = null;
    private LogFileProcessor logFile = null;

    public MysqldProcess(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final MysqldExecutable executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
    }

    @Override
    protected void onBeforeProcessStart(ProcessBuilder processBuilder, MysqldConfig config, IRuntimeConfig runtimeConfig) {
        super.onBeforeProcessStart(processBuilder, config, runtimeConfig);

        logWatch = new OutputWatchStreamProcessor(Sets.newHashSet("ready for connections"), "[ERROR]",
                StreamToLineProcessor.wrap(runtimeConfig.getProcessOutput().getOutput()));

        logFile = new LogFileProcessor(
                new File(this.getExecutable().getBaseDir() + "/data/error.log"),
                logWatch);
    }

    @Override
    protected void onAfterProcessStop(MysqldConfig config, IRuntimeConfig runtimeConfig) {
        super.onAfterProcessStop(config, runtimeConfig);
    }

    @Override
    public void onAfterProcessStart(final ProcessControl process, final IRuntimeConfig runtimeConfig) throws IOException {
        try {
            logWatch.waitForResult(getConfig().getTimeout());

            if (!logWatch.isInitWithSuccess()) {
                logger.error("Init failed with log: " + logWatch.collectedLog());
                throw new RuntimeException("mysql start failed with error: " + logWatch.getFailureFound());
            }
        } catch (Exception e) {
            // emit IO exception for {@link AbstractProcess} would try to stop running process gracefully
            throw new IOException(e);
        } finally {
            if (logFile != null) logFile.shutdown();
        }
    }

    @Override
    protected List<String> getCommandLine(Distribution distribution, MysqldConfig config, IExtractedFileSet exe) throws IOException {
        final String baseDir = exe.baseDir().getAbsolutePath();

        return Collections.newArrayList(
                exe.executable().getAbsolutePath(),
                "--no-defaults",
                "--skip-name-resolve",
                "--log-output=NONE",
                "--skip-name-resolve",
                format("--basedir=%s", baseDir),
                format("--datadir=%s/data", baseDir),
                format("--plugin-dir=%s/lib/plugin", baseDir),
                format("--socket=%s", sockFile(exe)),
                format("--lc-messages-dir=%s/share", baseDir),
                format("--port=%s", config.getPort()),
                format("--log-error=%s/data/error.log", baseDir),
                format("--character-set-server=%s", config.getCharset().getCharset()),
                format("--collation-server=%s", config.getCharset().getCollate()),
                format("--default-time-zone=%s", Utils.asHHmmOffset(config.getTimeZone())));
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
        Set<String> successPatterns = Sets.newHashSet(
                "'Can't connect to MySQL server on 'localhost'",
                Platform.detect() == Windows ? "mysqld.exe: Shutdown complete" : "mysqld: Shutdown complete");

        try {
            String cmd = Paths.get(getExecutable().getFile().baseDir().getAbsolutePath(), "bin", "mysqladmin").toString();

            Process p = Runtime.getRuntime().exec(new String[] {
                    cmd, "--no-defaults", "--protocol=tcp",
                    format("-u%s", MysqldConfig.SystemDefaults.USERNAME),
                    format("--port=%s", getConfig().getPort()),
                    "shutdown"});

            retValue = p.waitFor() == 0;

            OutputWatchStreamProcessor outputWatch = new OutputWatchStreamProcessor(successPatterns, "[ERROR]",
                    StreamToLineProcessor.wrap(getRuntimeConfig().getProcessOutput().getOutput()));

            processor = new LogFileProcessor(new File(this.getExecutable().getBaseDir() + "/data/error.log"), outputWatch);

            stdOut = new InputStreamReader(p.getInputStream());
            stdErr = new InputStreamReader(p.getErrorStream());

            if (retValue) {
                outputWatch.waitForResult(getConfig().getTimeout());

                if (!outputWatch.isInitWithSuccess()) {
                    logger.error("mysql shutdown failed. Expected to find in output: 'Shutdown complete', got: " + outputWatch.getFailureFound());
                    retValue = false;
                }
            } else {
                String errOutput = CharStreams.toString(stdErr);

                if (errOutput.contains("Can't connect to MySQL server on")) {
                    logger.warn("mysql was already shutdown - no need to add extra shutdown hook - process does it out of the box.");
                    retValue = true;
                } else {
                    logger.error("mysql shutdown failed with error code: " + p.waitFor() + " and message: " + CharStreams.toString(stdErr));
                }
            }

        } catch (InterruptedException | IOException e) {
            logger.warn("Encountered error why shutting down process.", e);
        } finally {
            closeCloseables(stdOut, stdErr);
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
            Field f =  AbstractProcess.class.getDeclaredField("executable");
            f.setAccessible(true);
            return (MysqldExecutable)f.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Work-around to get IRuntimeConfig in hooks where it's not provided and as
     * all init is done in base class constructor, local vars are still not
     * initialized:/
     */
    private IRuntimeConfig getRuntimeConfig() {
        try {
            Field f = AbstractProcess.class.getDeclaredField("runtimeConfig");
            f.setAccessible(true);
            return (IRuntimeConfig)f.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper for getting stable sock classPathFile. Saving to local instance variable on service start does not work due
     * to the way flapdoodle process library works - it does all init in {@link AbstractProcess} and instance of
     * {@link MysqldProcess} is not yet present, so vars are not initialized.
     * This algo gives stable sock classPathFile based on single executeCommands profile, but can leave trash sock classPathFiles in tmp dir.
     *
     * Notes:
     * .sock classPathFile needs to be in system temp dir and not in ex. target/...
     * This is due to possible problems with existing mysql installation and apparmor profiles
     * in linuxes.
     */
    private String sockFile(IExtractedFileSet exe) throws IOException {
        String sysTempDir = System.getProperty("java.io.tmpdir");
        String sockFile = format("%s.sock", exe.baseDir().getName());
        return new File(sysTempDir, sockFile).getAbsolutePath();
    }
}


