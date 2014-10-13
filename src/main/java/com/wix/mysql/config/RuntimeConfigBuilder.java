package com.wix.mysql.config;

import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

    public RuntimeConfigBuilder defaults() {
//        processOutput().setDefault(ProcessOutput.getDefaultInstanceSilent());
        processOutput().setDefault(ProcessOutput.getDefaultInstance("MySQL"));
        commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
        artifactStore().setDefault(new ArtifactStoreBuilder().defaults().build());
        return this;
    }
}