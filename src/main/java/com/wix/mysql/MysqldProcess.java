package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Service;
import com.wix.mysql.io.NotifyingStreamProcessor;
import com.wix.mysql.io.NotifyingStreamProcessor.ResultMatchingListener;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;

import static com.wix.mysql.utils.Utils.closeCloseables;
import static com.wix.mysql.utils.Utils.readToString;
import static de.flapdoodle.embed.process.distribution.Platform.Windows;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MysqldProcess extends AbstractProcess<MysqldConfig, MysqldExecutable, MysqldProcess> {

    private final static Logger logger = LoggerFactory.getLogger(MysqldProcess.class);

    private NotifyingStreamProcessor outputWatch;

    public MysqldProcess(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final MysqldExecutable executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
    }

    @Override
    public void onAfterProcessStart(final ProcessControl process, final IRuntimeConfig runtimeConfig) throws IOException {
        outputWatch = new NotifyingStreamProcessor(StreamToLineProcessor.wrap(runtimeConfig.getProcessOutput().getOutput()));
        Processors.connect(process.getReader(), outputWatch);
        Processors.connect(process.getError(), outputWatch);
        ResultMatchingListener startupListener = outputWatch.addListener(new ResultMatchingListener("ready for connections"));

        try {
            startupListener.waitForResult(getConfig().getTimeout(MILLISECONDS));

            if (!startupListener.isInitWithSuccess()) {
                throw new RuntimeException("mysql start failed with error: " + startupListener.getFailureFound());
            }
        } catch (Exception e) {
            // emit IO exception for {@link AbstractProcess} would try to stop running process gracefully
            throw new IOException(e);
        }
    }

    @Override
    protected List<String> getCommandLine(Distribution distribution, MysqldConfig config, IExtractedFileSet exe) throws IOException {
        return Service.commandLine(config, exe);
    }

    @Override
    protected synchronized void stopInternal() {
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

    @Override
    protected void cleanupInternal() {
    }

    private boolean stopUsingMysqldadmin() {
        ResultMatchingListener shutdownListener = outputWatch.addListener(new ResultMatchingListener(": Shutdown complete"));
        boolean retValue = false;
        Reader stdErr = null;

        try {
            String cmd = Paths.get(getExecutable().getFile().baseDir().getAbsolutePath(), "bin", "mysqladmin").toString();

            Process p = Runtime.getRuntime().exec(new String[]{
                    cmd, "--no-defaults", "--protocol=tcp",
                    format("-u%s", MysqldConfig.SystemDefaults.USERNAME),
                    format("--port=%s", getConfig().getPort()),
                    "shutdown"});

            //TODO: make wait with timeout
            retValue = p.waitFor() == 0;

            stdErr = new InputStreamReader(p.getErrorStream());

            if (retValue) {
                shutdownListener.waitForResult(getConfig().getTimeout(MILLISECONDS));

                //TODO: figure out a better strategy for this. It seems windows does not actually shuts down process after it says it does.
                if (Platform.detect() == Windows) {
                    Thread.sleep(2000);
                }

                if (!shutdownListener.isInitWithSuccess()) {
                    logger.error("mysql shutdown failed. Expected to find in output: 'Shutdown complete', got: " + shutdownListener.getFailureFound());
                    retValue = false;
                } else {
                    logger.debug("mysql shutdown succeeded.");
                    retValue = true;
                }

            } else {
                String errOutput = readToString(stdErr);

                if (errOutput.contains("Can't connect to MySQL server on")) {
                    logger.warn("mysql was already shutdown - no need to add extra shutdown hook - process does it out of the box.");
                    retValue = true;
                } else {
                    logger.error("mysql shutdown failed with error code: " + p.waitFor() + " and message: " + errOutput);
                }
            }

        } catch (InterruptedException | IOException e) {
            logger.warn("Encountered error why shutting down process.", e);
        } finally {
            closeCloseables(stdErr);
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
            return (MysqldExecutable) f.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
