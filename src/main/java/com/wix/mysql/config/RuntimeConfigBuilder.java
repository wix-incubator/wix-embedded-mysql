package com.wix.mysql.config;

import com.wix.mysql.MysqldProcess;
import com.wix.mysql.store.SafeExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.flapdoodle.embed.process.io.Processors.logTo;
import static de.flapdoodle.embed.process.io.Slf4jLevel.DEBUG;

public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

    private Logger logger = LoggerFactory.getLogger(MysqldProcess.class);
    private IStreamProcessor log = logTo(logger, DEBUG);

    public RuntimeConfigBuilder defaults(
            final MysqldConfig mysqldConfig,
            final DownloadConfig downloadConfig) {

        processOutput().setDefault(new ProcessOutput(log, log, log));
        commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
        artifactStore().setDefault(new SafeExtractedArtifactStoreBuilder().defaults(mysqldConfig, downloadConfig).build());

        return this;
    }
}