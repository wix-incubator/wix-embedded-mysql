package com.wix.mysql.config;

import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class MysqldConfig extends ExecutableProcessConfig {

    private final Integer port;
    private final Charset charset;
    private final User user;

    protected MysqldConfig(
            final IVersion version,
            final int port,
            final Charset charset,
            final User user) {
        super(version, new ISupportConfig() {
            public String getName() { return "mysqld"; }
            public String getSupportUrl() { return "https://github.com/wix/wix-embedded-mysql/issues"; }
            public String messageOnException(Class<?> context, Exception exception) {
                return "no message";
            }
        });

        this.port = port;
        this.charset = charset;
        this.user = user;
    }

    public Version getVersion() { return (Version)version; }
    public Charset getCharset() { return charset; }
    public int getPort() { return port; }
    public int getTimeout() { return 60000; }
    public String getUsername() { return user.name; }
    public String getPassword() { return user.password; }

    public static Builder aMysqldConfig(final Version version) { return new Builder(version); }

    public static class Builder {
        private IVersion version;
        private int port = 3310;
        private Charset charset = Charset.defaults();
        private User user = new User("auser", "sa");

        public Builder(IVersion version) { this.version = version; }

        public Builder withPort(int port) { this.port = port; return this; }
        public Builder withCharset(Charset charset) { this.charset = charset; return this; }
        public Builder withUser(String username, String password) { this.user = new User(username, password); return this; }

        public MysqldConfig build() { return new MysqldConfig(version, port, charset, user); }
    }

    protected static class User {
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