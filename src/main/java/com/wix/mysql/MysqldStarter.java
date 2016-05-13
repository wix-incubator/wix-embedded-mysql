package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Starter;

class MysqldStarter extends Starter<MysqldConfig, MysqldExecutable, MysqldProcess> {

    public MysqldStarter(final IRuntimeConfig config) {
        super(config);
    }

    @Override
    protected MysqldExecutable newExecutable(
            final MysqldConfig config,
            final Distribution distribution,
            final IRuntimeConfig runtime,
            final IExtractedFileSet exe) {
        return new MysqldExecutable(distribution, config, runtime, exe);
    }
}