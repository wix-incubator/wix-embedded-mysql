package com.wix.mysql.config;

import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

import java.util.Arrays;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldConfig extends ExecutableProcessConfig {

    private final Integer port;
    private final String username;
    private final String password;
    private final String[] schemas;

    public MysqldConfig(
            final IVersion version,
            final String username,
            final String password,
            final String schema,
            final Integer port)
    {
        this(version, username, password, (schema != null) ? new String[]{schema} : null, port);
    }

    public MysqldConfig(
            final IVersion version,
            final String username,
            final String password,
            final String[] schemas,
            final Integer port) {
        super(version, new ISupportConfig() {
            public String getName() {return "mysqld";}
            public String getSupportUrl() {return "https://github.com/wix/wix-embedded-mysql/issues";}
            public String messageOnException(Class<?> context, Exception exception) {
                return "no message";
            }});

        checkArgument(!isNullOrEmpty(username), "Username cannot be null or empty");
        checkArgument((schemas != null) && (schemas.length > 0), "Schemas cannot be empty");
        for( String scheme : schemas){
            checkArgument(scheme != null && scheme.trim().length() > 0, "Schema cannot be empty");
            checkArgument(!SystemDefaults.SCHEMA.equals(scheme), String.format("Usage of system schema '%s' is forbidden", SystemDefaults.SCHEMA));
        }
        checkArgument(port > 0, "Port must be a positive integer");

        this.username = username;
        this.password = password;
        this.schemas = schemas;
        this.port = port;
    }

    public MysqldConfig(
            final IVersion version,
            final Integer port) {
        this(version, SystemDefaults.USERNAME, SystemDefaults.PASSWORD, new String[]{SystemDefaults.SCHEMA}, port);
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    /**
     * @deprecated use {@link #getSchemas()} instead
     */
    public String getSchema() { return schemas[0]; }

    public String[] getSchemas() { return schemas; }
    public int getPort() { return port; }
    public int getTimeout() { return 30000; }

    public boolean shouldCreateUser() {
        return (!username.equals(SystemDefaults.USERNAME) && !Objects.equals(password, SystemDefaults.PASSWORD));
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MysqldConfig that = (MysqldConfig) o;

        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (!Arrays.equals(schemas, that.schemas)) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = port != null ? port.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (schemas != null ? Arrays.hashCode(schemas) : 0);
        return result;
    }

    public static class SystemDefaults {
        public final static String USERNAME = "root";
        public final static String PASSWORD = null;
        public final static String SCHEMA = "information_schema";
    }

    public static class Defaults {
        public final static Integer PORT = 3306;
        public final static String USERNAME = "auser";
        public final static String PASSWORD = "sa";
        public final static String SCHEMA = "test_db";
    }

}