package com.wix.mysql;

import com.wix.mysql.config.DownloadConfig;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.RuntimeConfigBuilder;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;

import static com.wix.mysql.config.DownloadConfig.aDownloadConfig;

public class MysqlDownloadAndExtract {

    public static void main(String[] args) {
        DownloadConfig downloadConfig = aDownloadConfig().withCacheDir(args[0]).build();
        MysqldConfig mysqldConfig = MysqldConfig.aMysqldConfig(Version.valueOf(version(args))).build();
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(mysqldConfig, downloadConfig).build();
        MysqldStarter mysqldStarter = new MysqldStarter(runtimeConfig);
        mysqldStarter.prepare(mysqldConfig);
    }

    private static String version(final String[] args) {
        final String majorVersion = args[1];
        final String minorVersion = args.length > 2 ? args[2] : "latest";
        return "v" + majorVersion.replace('.', '_') + "_" + minorVersion;
    }
}
