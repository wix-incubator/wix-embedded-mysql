package com.wix.mysql.config;

import com.google.common.base.Objects;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

import java.util.Arrays;

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
            final Integer port) {
        this(version, username, password, new String[]{ schema }, port);
    }

    public MysqldConfig(
            final IVersion version,
            final String username,
            final String password,
            final String[] schemas,
            final Integer port) {

        super(version, new ISupportConfig() {
            public String getName() { return "mysqld"; }
            public String getSupportUrl() { return "https://github.com/wix/wix-embedded-mysql/issues"; }
            public String messageOnException(Class<?> context, Exception exception) {
                return "no message";
            }
        });

        checkArgument(!isNullOrEmpty(username), "Username cannot be null or empty");
        checkArgument((schemas != null) && (schemas.length > 0), "Schemas cannot be empty");
        checkArgument(!((username == SystemDefaults.USERNAME) && (password != SystemDefaults.PASSWORD)), "Cannot use custom password for 'root' user'");
        for( String schema : schemas){
            checkArgument(!isNullOrEmpty(schema), "Schema cannot be empty");
            checkArgument(!SystemDefaults.SCHEMA.equals(schema), String.format("Usage of system schema '%s' is forbidden", SystemDefaults.SCHEMA));
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
    public int getTimeout() { return 60000; }

    public boolean shouldCreateUser() {
        return (!username.equals(SystemDefaults.USERNAME) && !java.util.Objects.equals(password, SystemDefaults.PASSWORD));
    }

    @Override
    public String toString() {
        return "MysqldConfig{" +
                "port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", schemas=" + Arrays.toString(schemas) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MysqldConfig that = (MysqldConfig) o;

        return Objects.equal(this.port, that.port) &&
                Objects.equal(this.username, that.username) &&
                Objects.equal(this.password, that.password) &&
                Objects.equal(this.schemas, that.schemas) &&
                Objects.equal(this.version, that.version) &&
                Objects.equal(this.supportConfig(), that.supportConfig());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(port, username, password, schemas, version, this.supportConfig());
    }

    public static class SystemDefaults {
        public final static String USERNAME = "root";
        public final static String PASSWORD = null;
        public final static String SCHEMA = "information_schema";
    }

    public static class Defaults {
        public final static Integer PORT = 3310;
        public final static String USERNAME = "auser";
        public final static String PASSWORD = "sa";
        public final static String SCHEMA = "test_db";
    }

    public static MysqldConfigBuilder Builder(Version version) { return new MysqldConfigBuilder(version); }

}