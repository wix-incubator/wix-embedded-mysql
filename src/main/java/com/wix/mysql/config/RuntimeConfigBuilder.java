package com.wix.mysql.config;

import com.wix.mysql.MysqldProcess;
import com.wix.mysql.distribution.Version;
import com.wix.mysql.embed.process.store.SafeExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.store.IArtifactStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.flapdoodle.embed.process.io.Processors.logTo;
import static de.flapdoodle.embed.process.io.Slf4jLevel.DEBUG;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

    private Logger logger = LoggerFactory.getLogger(MysqldProcess.class);

    public RuntimeConfigBuilder defaults() {
        processOutput().setDefault(new ProcessOutput(logTo(logger, DEBUG), logTo(logger, DEBUG), logTo(logger, DEBUG)));
        commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
        artifactStore().setDefault(artifactStoreBuilderFor("mysql-default"));
        return this;
    }

    public RuntimeConfigBuilder defaults(Version version) {
        String directoryName = String.format("mysql-%s", version.getMajorVersion());
        defaults().artifactStore().setDefault(artifactStoreBuilderFor(directoryName));

        return this;
    }

    private IArtifactStore artifactStoreBuilderFor(String directoryName) {
        return new SafeExtractedArtifactStoreBuilder().defaults(directoryName).build();
    }
}