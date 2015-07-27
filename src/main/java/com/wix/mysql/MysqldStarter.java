package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Starter;

/**
 * @author viliusl
 * @since 27/09/14
 */
class MysqldStarter extends Starter<MysqldConfig, MysqldExecutable, MysqldProcess>{

    public MysqldStarter(final IRuntimeConfig config) {
        super(config);
    }

    @Override
    protected MysqldExecutable newExecutable(
            final MysqldConfig config,
            final Distribution distribution,
            final IRuntimeConfig runtime,
            final IExtractedFileSet exe) {
        return  new MysqldExecutable(distribution, config, runtime, exe);
    }

    public static MysqldStarter instance(final IRuntimeConfig config) {
        return new MysqldStarter(config);
    }

    public static MysqldStarter defaultInstance() {
        return new MysqldStarter(new RuntimeConfigBuilder().defaults().build());
    }
}