package com.wixpress.embed.mysql;

import com.wixpress.embed.mysql.config.MysqldConfig;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.*;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.ProcessControl;

import java.io.IOException;
import java.io.InputStreamReader;
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

    private final Logger log = Logger.getLogger(this.getClass().getName());
    private boolean stopped = false;
    private final int timeout;
    final MysqldExecutable executable;
    final MysqldConfig config;

    public MysqldProcess(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final MysqldExecutable executable,
            final int timeout) throws IOException {
        super(distribution, config, runtimeConfig, executable);
        this.timeout = timeout;
        this.executable = executable;
        this.config = config;
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
                String.format("--port=%s", config.getPort()),
                "--console");//windows specific
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
    protected void cleanupInternal() {

    }

    @Override
    public void onAfterProcessStart(final ProcessControl process, final IRuntimeConfig runtimeConfig) throws IOException {
        Set<String> errors = new HashSet<String>();
        errors.add("[ERROR]");
        LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(
                "ready for connections",
                errors,
                StreamToLineProcessor.wrap(runtimeConfig.getProcessOutput().getOutput()));

        Processors.connect(process.getReader(), logWatch);
        Processors.connect(process.getError(), logWatch);

        logWatch.waitForResult(timeout);

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
                String.format("--port=%s", config.getPort()),
                "shutdown"},
                null,
                executable.getFile().generatedBaseDir());

            Processors.connect(new InputStreamReader(p.getInputStream()), Processors.logTo(log, Level.FINER));
            Processors.connect(new InputStreamReader(p.getErrorStream()), Processors.logTo(log, Level.FINER));

            int retCode = p.waitFor();

            if (retCode != 0) {
                throw new RuntimeException(String.format("'bin/mysqladmin' stop command exited with error code: %s", retCode));
            } else
                return true;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
