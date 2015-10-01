package com.wix.mysql.config;

import com.wix.mysql.MysqldProcess;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.Slf4jLevel;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.flapdoodle.embed.process.io.Processors.logTo;
import static de.flapdoodle.embed.process.io.Slf4jLevel.DEBUG;
import static de.flapdoodle.embed.process.io.Slf4jLevel.ERROR;
import static de.flapdoodle.embed.process.io.Slf4jLevel.INFO;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

    private Logger logger = LoggerFactory.getLogger(MysqldProcess.class);

    public RuntimeConfigBuilder defaults() {
        processOutput().setDefault(new ProcessOutput(logTo(logger, DEBUG), logTo(logger, DEBUG), logTo(logger, DEBUG)));
        commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
        artifactStore().setDefault(new ArtifactStoreBuilder().defaults().build());
        return this;
    }
}