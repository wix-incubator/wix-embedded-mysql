package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Setup;
import com.wix.mysql.distribution.setup.FilePermissionsInitializer;
import com.wix.mysql.distribution.setup.Initializer;
import com.wix.mysql.distribution.setup.Mysql57Initializer;
import com.wix.mysql.distribution.setup.NixBefore57Initializer;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author viliusl
 * @since 27/09/14
 */
class MysqldExecutable extends Executable<MysqldConfig, MysqldProcess> {

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
        Setup.apply(config.getVersion(), executable);

        return new MysqldProcess(distribution, config, runtime, this);
    }

    File getBaseDir() {
        return executable.baseDir();
    }
}
