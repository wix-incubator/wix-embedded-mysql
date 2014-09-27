package com.wixpress.embed.mysql.config;

import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

    public RuntimeConfigBuilder defaults() {
        processOutput().setDefault(new ProcessOutput(Processors.console(), Processors.console(), Processors.console()));
        commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
        artifactStore().setDefault(new ArtifactStoreBuilder().defaults().build());
        return this;
    }
}