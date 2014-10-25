package com.wix.mysql;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldProcess extends AbstractProcess<MysqldConfig, MysqldExecutable, MysqldProcess> {

    private final Logger log = Logger.getLogger("MysqldProcess");
    private boolean stopped = false;

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
            if (!stopped) {
                stopped = true;

                log.info("try to stop mysqld");
                if (!stopUsingMysqldadmin()) {
                    log.warning("could not stop mysqld via mysqladmin, try next");
                    if (!sendKillToProcess()) {
                        log.warning("could not stop mysqld, try next");
                        if (!sendTermToProcess()) {
                            log.warning("could not stop mysqld, try next");
                            if (!tryKillToProcess()) {
                                log.warning("could not stop mysqld the second time, try one last thing");
                            }
                        }
                    }
                    stopProcess();
                }
            }
        }

    }

    @Override
    protected void cleanupInternal() {}

    @Override
    public void onAfterProcessStart(final ProcessControl process, final IRuntimeConfig runtimeConfig) throws IOException {
        Set<String> errors = new HashSet<String>();
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
    }

    private boolean stopUsingMysqldadmin() {
        try {
            Process p = Runtime.getRuntime().exec(new String[] {
                "bin/mysqladmin",
                "-uroot",//user, should be different if auth method is different, password is needed as well
                "-hlocalhost",
                "--protocol=tcp",
                String.format("--port=%s", getConfig().getPort()),
                "shutdown"},
                null,
                getExecutable().getFile().generatedBaseDir());

            Processors.connect(new InputStreamReader(p.getInputStream()), Processors.logTo(log, Level.INFO));
            Processors.connect(new InputStreamReader(p.getErrorStream()), Processors.logTo(log, Level.INFO));

            return p.waitFor() == 0;
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "Encountered error why shutting down process.", e);
            return false;
        } catch (IOException e) {
            log.log(Level.WARNING, "Encountered error why shutting down process.", e);
            return false;
        }
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
     * .sock file needs to be in system temp dir and not in ex. target/...
     *
     * This is due to possible problems with existing mysql installation and apparmor profiles
     * in linuxes.
     */
    private String sockFile(IExtractedFileSet exe) throws IOException {
        File f = Files.createTempFile("mysql", "sock").toFile();
        String path = f.getAbsolutePath();
        f.delete();
        return path;
    }

}
