package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Setup;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

class MysqldExecutable extends Executable<MysqldConfig, MysqldProcess> {

    private final static Logger logger = LoggerFactory.getLogger(MysqldExecutable.class);

    private final IExtractedFileSet executable;

    MysqldExecutable(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final IExtractedFileSet executable) {
        super(distribution, config, runtimeConfig, executable);
        this.executable = executable;
    }

    @Override
    protected MysqldProcess start(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtime) throws IOException {
        logger.info("Preparing mysqld for startup");
        Setup.apply(config, executable, runtime);
        logger.info("Starting MysqldProcess");
        return new MysqldProcess(distribution, config, runtime, this);
    }

    File getBaseDir() {
        return executable.baseDir();
    }
}
