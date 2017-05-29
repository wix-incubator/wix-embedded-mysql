package com.wix.mysql.config;

import com.wix.mysql.MysqldProcess;
import com.wix.mysql.distribution.Version;
import com.wix.mysql.store.SafeExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.store.IArtifactStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.UUID;

import static de.flapdoodle.embed.process.io.Processors.logTo;
import static de.flapdoodle.embed.process.io.Slf4jLevel.DEBUG;

public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

    private Logger logger = LoggerFactory.getLogger(MysqldProcess.class);
    private IStreamProcessor log = logTo(logger, DEBUG);

    public RuntimeConfigBuilder defaults(
            final Version version,
            final ArtifactStoreConfig artifactStoreConfig) {

        processOutput().setDefault(new ProcessOutput(log, log, log));
        commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
        artifactStore().setDefault(artifactStoreBuilderFor(version, artifactStoreConfig));

        return this;
    }

    private IArtifactStore artifactStoreBuilderFor(
            final Version version,
            final ArtifactStoreConfig artifactStoreConfig) {

        String tempExtractDir = String.format("mysql-%s-%s", version.getMajorVersion(), UUID.randomUUID());
        String combinedPath = new File(artifactStoreConfig.getTempDir(), tempExtractDir).getPath();
        return new SafeExtractedArtifactStoreBuilder().defaults(combinedPath).build();
    }
}