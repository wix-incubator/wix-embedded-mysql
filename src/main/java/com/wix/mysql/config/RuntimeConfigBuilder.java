package com.wix.mysql.config;

import com.wix.mysql.MysqldProcess;
import com.wix.mysql.distribution.Version;
import com.wix.mysql.store.SafeExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.flapdoodle.embed.process.io.Processors.logTo;
import static de.flapdoodle.embed.process.io.Slf4jLevel.DEBUG;
import static java.lang.String.format;

public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

    private Logger logger = LoggerFactory.getLogger(MysqldProcess.class);

    public RuntimeConfigBuilder defaults(
            final Version version,
            final DownloadConfig downloadConfig) {
        SafeExtractedArtifactStoreBuilder artifactStoreBuilder = artifactStoreBuilderFor(version, downloadConfig);
        processOutput(new ProcessOutput(logTo(logger, DEBUG), logTo(logger, DEBUG), logTo(logger, DEBUG)));
        commandLinePostProcessor(new ICommandLinePostProcessor.Noop());

        artifactStore(artifactStoreBuilder);

        return this;
    }

    private SafeExtractedArtifactStoreBuilder artifactStoreBuilderFor(Version version, DownloadConfig downloadConfig) {
        String directoryName = format("mysql-%s", version.getMajorVersion());
        return (SafeExtractedArtifactStoreBuilder) new SafeExtractedArtifactStoreBuilder().defaults(directoryName, downloadConfig);
    }
}