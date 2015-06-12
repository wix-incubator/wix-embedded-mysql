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

    protected MysqldConfig(
            IVersion version,
            int port,
            Charset charset) {
        super(version, new ISupportConfig() {
            public String getName() { return "mysqld"; }
            public String getSupportUrl() { return "https://github.com/wix/wix-embedded-mysql/issues"; }
            public String messageOnException(Class<?> context, Exception exception) {
                return "no message";
            }
        });

        this.port = port;
        this.charset = charset;
    }

    public Version getVersion() { return (Version)version; }
    public Charset getCharset() { return charset; }
    public int getPort() { return port; }
    public int getTimeout() { return 60000; }

    public static class SystemDefaults {
        public final static String USERNAME = "root";
        public final static String SCHEMA = "information_schema";
    }

    public static Builder Builder(final Version version) { return new Builder(version); }
    public static MysqldConfig defaults(final Version version) {return new Builder(version).build(); }

    public static class Builder {
        private IVersion version;
        private int port = 3310;
        private Charset charset = Charset.defaults();

        public Builder(IVersion version) { this.version = version; }

        public Builder withPort(int port) { this.port = port; return this; }
        public Builder withCharset(Charset charset) { this.charset = charset; return this; }

        public MysqldConfig build() { return new MysqldConfig(version, port, charset); }
    }

}