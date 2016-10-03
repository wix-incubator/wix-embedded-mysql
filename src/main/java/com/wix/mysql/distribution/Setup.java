package com.wix.mysql.distribution;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.setup.FilePermissionsInitializer;
import com.wix.mysql.distribution.setup.Initializer;
import com.wix.mysql.distribution.setup.Mysql57Initializer;
import com.wix.mysql.distribution.setup.NixBefore57Initializer;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.IOException;
import java.util.List;

public class Setup {

    private static List<Initializer> initializers = Collections.newArrayList(
            new FilePermissionsInitializer(),
            new Mysql57Initializer(),
            new NixBefore57Initializer());


    public static void apply(MysqldConfig config, IExtractedFileSet files, IRuntimeConfig runtimeConfig) throws IOException {
        for (Initializer initializer : initializers) {
            if (initializer.matches(config.getVersion())) {
                initializer.apply(files, runtimeConfig, config);
            }
        }
    }
}
