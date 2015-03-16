package com.wix.mysql.config;

import de.flapdoodle.embed.process.distribution.IVersion;

import static com.wix.mysql.config.MysqldConfig.Defaults;

/**
 * @author viliusl
 * @since 06/11/14
 */
public class MysqldConfigBuilder {

    private IVersion version;
    private String username = Defaults.USERNAME;
    private String password = Defaults.PASSWORD;
    private String[] schemas = new String[]{Defaults.SCHEMA};
    private int port = Defaults.PORT;

    public MysqldConfigBuilder(IVersion version) { this.version = version; }

    public MysqldConfigBuilder withUsername(String username) { this.username = username; return this; }
    public MysqldConfigBuilder withPassword(String password) { this.password = password; return this; }
    public MysqldConfigBuilder withSchema(String schemaName) { this.schemas = new String[]{schemaName}; return this; }
    public MysqldConfigBuilder withSchemas(String[] schemas) { this.schemas = schemas; return this; }
    public MysqldConfigBuilder withPort(int port) { this.port = port; return this; }

    public MysqldConfig build() { return new MysqldConfig(version, username, password, schemas, port); }
}
