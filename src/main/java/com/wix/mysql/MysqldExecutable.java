package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.initializers.FilePermissionsInitializer;
import com.wix.mysql.distribution.initializers.Initializer;
import com.wix.mysql.distribution.initializers.Mysql57Initializer;
import com.wix.mysql.distribution.initializers.NixBefore57Initializer;
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

    private List<Initializer> initializers = new ArrayList<>();

    MysqldExecutable(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtimeConfig,
            final IExtractedFileSet executable) {
        super(distribution, config, runtimeConfig, executable);
        this.executable = executable;

        initializers.add(new FilePermissionsInitializer());
        initializers.add(new Mysql57Initializer());
        initializers.add(new NixBefore57Initializer());
    }

    @Override
    protected MysqldProcess start(
            final Distribution distribution,
            final MysqldConfig config,
            final IRuntimeConfig runtime) throws IOException {

        for (Initializer initializer : initializers) {
            if (initializer.matches(config.getVersion())) {
                initializer.apply(executable);
            }
        }

        return new MysqldProcess(distribution, config, runtime, this);
    }

    File getBaseDir() {
        return executable.baseDir();
    }
}
