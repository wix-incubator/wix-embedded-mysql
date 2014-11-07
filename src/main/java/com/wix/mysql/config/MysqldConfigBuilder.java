package com.wix.mysql.config;

import de.flapdoodle.embed.process.builder.AbstractBuilder;
import de.flapdoodle.embed.process.builder.TypedProperty;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * @author viliusl
 * @since 06/11/14
 */
public class MysqldConfigBuilder extends AbstractBuilder<MysqldConfig> {

    protected static final TypedProperty<IVersion> VERSION = TypedProperty.with("Username", IVersion.class);
    protected static final TypedProperty<String> USERNAME = TypedProperty.with("Username", String.class);
    protected static final TypedProperty<String> PASSWORD = TypedProperty.with("Password", String.class);
    protected static final TypedProperty<String> SCHEMA_NAME = TypedProperty.with("SchemaName", String.class);
    protected static final TypedProperty<Integer> PORT = TypedProperty.with("Port", Integer.class);

    public MysqldConfigBuilder(IVersion version) {
        property(VERSION).setDefault(version);
    }

    public MysqldConfigBuilder withUsername(String username) { set(USERNAME, username); return this; }
    public MysqldConfigBuilder withPassword(String password) { set(PASSWORD, password); return this; }
    public MysqldConfigBuilder withSchema(String schemaName) { set(SCHEMA_NAME, schemaName); return this; }
    public MysqldConfigBuilder withPort(int port) { set(PORT, port); return this; }

    @Override
    public MysqldConfig build() {
        return new MysqldConfig(
                get(VERSION),
                get(USERNAME, MysqldConfig.Defaults.USERNAME),
                get(PASSWORD, MysqldConfig.Defaults.PASSWORD),
                get(SCHEMA_NAME, MysqldConfig.Defaults.SCHEMA),
                get(PORT, MysqldConfig.Defaults.PORT));
    }
}
