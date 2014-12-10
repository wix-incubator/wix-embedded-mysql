package com.wix.mysql.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

import java.util.Objects;

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

        Preconditions.checkArgument(!Strings.isNullOrEmpty(username), "Username cannot be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(schema), "Schema cannot be null or empty");
        Preconditions.checkArgument(!schema.equals(SystemDefaults.SCHEMA), String.format("Usage of system schema '%s' is forbidden", SystemDefaults.SCHEMA));
        Preconditions.checkArgument(port > 0, "Port must be a positive integer");

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

        this.username = SystemDefaults.USERNAME;
        this.password = SystemDefaults.PASSWORD;
        this.schema = SystemDefaults.SCHEMA;
        this.port = port;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getSchema() { return schema; }
    public int getPort() { return port; }
    public int getTimeout() { return 30000; }

    public boolean shouldCreateUser() {
        return (!username.equals(SystemDefaults.USERNAME) && !Objects.equals(password, SystemDefaults.PASSWORD));
    }

    public static class SystemDefaults {
        public final static Integer PORT = 3306;
        public final static String USERNAME = "root";
        public final static String PASSWORD = null;
        public final static String SCHEMA = "information_schema";
    }

    public static class Defaults {
        public final static Integer PORT = SystemDefaults.PORT;
        public final static String USERNAME = "auser";
        public final static String PASSWORD = "sa";
        public final static String SCHEMA = "test_db";
    }

}