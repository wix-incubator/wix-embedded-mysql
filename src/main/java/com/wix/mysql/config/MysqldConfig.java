package com.wix.mysql.config;

import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldConfig extends ExecutableProcessConfig {

    private int port = 3306;

    public MysqldConfig(IVersion version, int port) {
        super(version, new ISupportConfig() {
            public String getName() { return "mysqld"; }
            public String getSupportUrl() { return "https://github.com/wix/wix-embedded-mysql/issues"; }
            public String messageOnException(Class<?> context, Exception exception) {
                return "no message";
            }
        });
        this.port = port;
    }

    public int getPort() { return port; }
    public int getTimeout() { return 30000; }
}