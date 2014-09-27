package com.wixpress.embed.mysql;

import com.wixpress.embed.mysql.config.MysqldConfig;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;

import java.io.IOException;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldExecutable extends Executable<MysqldConfig, MysqldProcess> {

    public MysqldExecutable(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final IExtractedFileSet executable) {
        super(distribution, config, runtimeConfig, executable);
    }

    @Override
    protected MysqldProcess start(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtime) throws IOException {
        return new MysqldProcess(distribution, config, runtime, this);
    }
}


