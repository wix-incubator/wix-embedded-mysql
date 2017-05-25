package com.wix.mysql.config;

import com.wix.mysql.MysqldProcess;
import com.wix.mysql.store.SafeExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static de.flapdoodle.embed.process.io.Processors.logTo;
import static de.flapdoodle.embed.process.io.Slf4jLevel.DEBUG;

public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

    private Logger logger = LoggerFactory.getLogger(MysqldProcess.class);
    private ArtifactStoreBuilder artifactStoreBuilder;

    public RuntimeConfigBuilder defaults() {
        this.artifactStoreBuilder = new SafeExtractedArtifactStoreBuilder().directory("mysql-default").defaults();
        processOutput().setDefault(new ProcessOutput(logTo(logger, DEBUG), logTo(logger, DEBUG), logTo(logger, DEBUG)));
        commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
        artifactStore().setDefault(artifactStoreBuilder.build());
        return this;
    }

    public RuntimeConfigBuilder withConfig(MysqldConfig config) {
        String directoryName = String.format("mysql-%s-%s", config.getVersion().getMajorVersion(), UUID.randomUUID());
        artifactStoreBuilder.directory(directoryName);
        artifactStoreBuilder.downloadPath(config.getDownloadPath());
        artifactStore().setDefault(artifactStoreBuilder.build());

        return this;
    }

}