package com.wix.mysql.config;

import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldConfig extends ExecutableProcessConfig {

    private final Integer port;
    private final String username;
    private final String password;
    private final String schema;

    public MysqldConfig(
            final IVersion version,
            final String username,
            final String password,
            final String schema,
            final Integer port) {
        super(version, new ISupportConfig() {
            public String getName() {return "mysqld";}
            public String getSupportUrl() {return "https://github.com/wix/wix-embedded-mysql/issues";}
            public String messageOnException(Class<?> context, Exception exception) {
                return "no message";
            }});

        this.username = username;
        this.password = password;
        this.schema = schema;
        this.port = port;
    }

    public MysqldConfig(
            final IVersion version,
            final Integer port) {
        super(version, new ISupportConfig() {
            public String getName() {return "mysqld";}
            public String getSupportUrl() {return "https://github.com/wix/wix-embedded-mysql/issues";}
            public String messageOnException(Class<?> context, Exception exception) {
                return "no message";
            }});

        this.username = Defaults.USERNAME;
        this.password = Defaults.PASSWORD;
        this.schema = Defaults.SCHEMA;
        this.port = port;
    }

    public String getUsername() { return username; }
    public boolean isDefaultUsername() {return username.equals(Defaults.USERNAME); }

    public String getPassword() { return password; }
    public boolean isDefaultPassword() {return password.equals(Defaults.PASSWORD); }

    public String getSchema() { return schema; }
    public boolean isDefaultSchema() {return schema.equals(Defaults.SCHEMA); }

    public int getPort() { return port; }
    public boolean isDefaultPort() {return port.equals(Defaults.PORT); }

    public int getTimeout() { return 30000; }

    public static class Defaults {
        public final static Integer PORT = 3306;
        public final static String USERNAME = "root";
        public final static String PASSWORD = null;
        public final static String SCHEMA = "information_schema";
    }
}