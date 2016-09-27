package com.wix.mysql.config;

import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MysqldConfig extends ExecutableProcessConfig {

    private final int port;
    private final Charset charset;
    private final User user;
    private final TimeZone timeZone;
    private final int timeoutMs;

    protected MysqldConfig(
            final IVersion version,
            final int port,
            final Charset charset,
            final User user,
            final TimeZone timeZone,
            final int timeoutMs) {
        super(version, new ISupportConfig() {
            public String getName() {
                return "mysqld";
            }

            public String getSupportUrl() {
                return "https://github.com/wix/wix-embedded-mysql/issues";
            }

            public String messageOnException(Class<?> context, Exception exception) {
                return "no message";
            }
        });

        if (user.name.equals("root")) {
            throw new IllegalArgumentException("Usage of username 'root' is forbidden as it's reserved for system use");
        }

        this.port = port;
        this.charset = charset;
        this.user = user;
        this.timeZone = timeZone;
        this.timeoutMs = timeoutMs;
    }

    public Version getVersion() {
        return (Version) version;
    }

    public Charset getCharset() {
        return charset;
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeoutMs;
    }

    public String getUsername() {
        return user.name;
    }

    public String getPassword() {
        return user.password;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public static Builder aMysqldConfig(final Version version) {
        return new Builder(version);
    }

    public static class Builder {
        private IVersion version;
        private int port = 3310;
        private Charset charset = Charset.defaults();
        private User user = new User("auser", "sa");
        private TimeZone timeZone = TimeZone.getTimeZone("UTC");
        private int timeoutMs = 60000;

        public Builder(IVersion version) {
            this.version = version;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withTimeout(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder withCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder withUser(String username, String password) {
            this.user = new User(username, password);
            return this;
        }

        public Builder withTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder withTimeZone(String timeZoneId) {
            return withTimeZone(TimeZone.getTimeZone(timeZoneId));
        }

        public MysqldConfig build() {
            return new MysqldConfig(version, port, charset, user, timeZone, timeoutMs);
        }
    }

    public static class User {
        private final String name;
        private final String password;

        public User(String name, String password) {
            this.name = name;
            this.password = password;
        }
    }

    public static class SystemDefaults {
        public final static String USERNAME = "root";
        public final static String SCHEMA = "information_schema";
    }

}
