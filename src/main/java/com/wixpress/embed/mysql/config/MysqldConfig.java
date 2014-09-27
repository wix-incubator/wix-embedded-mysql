package com.wixpress.embed.mysql.config;

import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldConfig extends ExecutableProcessConfig {

    private int port = 3306;

    public MysqldConfig(IVersion version) {
        super(version, new MysqldSupportConfig());
    }

    public MysqldConfig(IVersion version, int port) {
        super(version, new MysqldSupportConfig());
        this.port = port;
    }

    public int getPort() { return port; }
    public int getTimeout() { return 6000; }


    public static class MysqldSupportConfig implements ISupportConfig {
        @Override
        public String getName() {
            return "mysqld";
        }

        @Override
        public String getSupportUrl() {
            return "https://github.com/wix/wix-embedded-mysql/issues";
        }

        @Override
        public String messageOnException(Class<?> context, Exception exception) {
            return null;
        }
    }
}