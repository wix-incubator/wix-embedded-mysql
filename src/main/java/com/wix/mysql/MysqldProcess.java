package com.wix.mysql;

import com.google.common.collect.Sets;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.input.LogFileProcessor;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.LogWatchStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldProcess extends AbstractProcess<MysqldConfig, MysqldExecutable, MysqldProcess> {

    private final static Logger logger = LoggerFactory.getLogger(MysqldProcess.class);



    public MysqldProcess(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final MysqldExecutable executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
    }

    @Override
    protected List<String> getCommandLine(Distribution distribution, MysqldConfig config, IExtractedFileSet exe) throws IOException {
        final String baseDir = exe.generatedBaseDir().getAbsolutePath();

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
        //"--console");//does not properly work, dodgy between versions.
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

    @Override
    public void onAfterProcessStart(final ProcessControl process, final IRuntimeConfig runtimeConfig) throws IOException {
        Set<String> errors = Sets.newHashSet();

        errors.add("[ERROR]");
        LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(
                "ready for connections",
                errors,
                StreamToLineProcessor.wrap(runtimeConfig.getProcessOutput().getOutput()));

        new LogFileProcessor(
                new File(this.getExecutable().executable.generatedBaseDir() + "/data/error.log"),
                logWatch);

        logWatch.waitForResult(getConfig().getTimeout());

        if (!logWatch.isInitWithSuccess()) {
            throw new RuntimeException("mysql start failed with error: " + logWatch.getFailureFound());
        }

        try {
            new MysqlConfigurer(getConfig()).configure();
        } catch (Exception e) {
            // emit IO exception for {@link AbstractProcess} would try to stop running process gracefully
            throw new IOException(e);
        }
    }

    private boolean stopUsingMysqldadmin() {
        boolean retValue = false;

        try {
            Process p = Runtime.getRuntime().exec(new String[] {
                            "bin/mysqladmin",
                            "--no-defaults",
                            String.format("-u%s", MysqldConfig.SystemDefaults.USERNAME),
                            "--protocol=socket",
                            String.format("--socket=%s", sockFile(getExecutable().executable)),
                            "shutdown"},
                    null,
                    getExecutable().getFile().generatedBaseDir());

            java.util.logging.Logger processLog = java.util.logging.Logger.getLogger(MysqldProcess.class.getName());
            Processors.connect(new InputStreamReader(p.getInputStream()), Processors.logTo(processLog, Level.INFO));
            Processors.connect(new InputStreamReader(p.getErrorStream()), Processors.logTo(processLog, Level.INFO));
            retValue = p.waitFor() == 0;
        } catch (InterruptedException e) {
            logger.warn("Encountered error why shutting down process.", e);
        } catch (IOException e) {
            logger.warn("Encountered error why shutting down process.", e);
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
        String pidFile = String.format("%s.sock", exe.generatedBaseDir().getName());
        return new File(sysTempDir, pidFile).getAbsolutePath();
    }

}
