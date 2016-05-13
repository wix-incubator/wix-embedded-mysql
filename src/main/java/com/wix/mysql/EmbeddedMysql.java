package com.wix.mysql;

import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfig.SystemDefaults;
import com.wix.mysql.config.RuntimeConfigBuilder;
import com.wix.mysql.config.SchemaConfig;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.wix.mysql.config.MysqldConfig.SystemDefaults.SCHEMA;
import static com.wix.mysql.utils.Utils.or;
import static java.lang.String.format;

public class EmbeddedMysql {
    protected final MysqldConfig config;
    protected final MysqldExecutable executable;
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    protected EmbeddedMysql(final MysqldConfig config) {
        this.config = config;
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(config.getVersion()).build();
        this.executable = new MysqldStarter(runtimeConfig).prepare(config);

        try {
            executable.start();
            getClient(SCHEMA).executeCommands(
                    format("CREATE USER '%s'@'%%' IDENTIFIED BY '%s';", config.getUsername(), config.getPassword()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MysqldConfig getConfig() {
        return this.config;
    }

    /** @deprecated Use overload with SchemaConfig */
    public void reloadSchema(final String schemaName, final File... scripts) {
        reloadSchema(SchemaConfig.aSchemaConfig(schemaName).withScripts(scripts).build());
    }

    /** @deprecated Use overload with SchemaConfig */
    public void reloadSchema(final String schemaName, final List<File> scripts) {
        reloadSchema(SchemaConfig.aSchemaConfig(schemaName).withScripts(scripts).build());
    }

    public void reloadSchema(final SchemaConfig config) {
        dropSchema(config);
        addSchema(config);
    }

    public void dropSchema(final SchemaConfig config) {
        getClient(SystemDefaults.SCHEMA).executeCommands(format("DROP DATABASE %s", config.getName()));
    }

    public EmbeddedMysql addSchema(final SchemaConfig schema) {
        Charset effectiveCharset = or(schema.getCharset(), config.getCharset());

        getClient(SystemDefaults.SCHEMA).executeCommands(
                format("CREATE DATABASE %s CHARACTER SET = %s COLLATE = %s;",
                        schema.getName(), effectiveCharset.getCharset(), effectiveCharset.getCollate()),
                format("GRANT ALL ON %s.* TO '%s'@'%%';", schema.getName(), config.getUsername()));

        MysqlClient client = getClient(schema.getName());
        client.executeScripts(schema.getScripts());
        client.executeCommands(schema.getCommands());

        return this;
    }

    public synchronized void stop() {
        if (isRunning.getAndSet(false)) {
            executable.stop();
        }
    }

    private MysqlClient getClient(final String schemaName) {
        return new MysqlClient(config, executable, schemaName);
    }


    public static Builder anEmbeddedMysql(final Version version) {
        return new Builder(MysqldConfig.aMysqldConfig(version).build());
    }

    public static Builder anEmbeddedMysql(final MysqldConfig config) {
        return new Builder(config);
    }

    public static class Builder {
        private final MysqldConfig config;
        private List<SchemaConfig> schemas = new ArrayList<>();

        public Builder(final MysqldConfig config) {
            this.config = config;
        }

        public Builder addSchema(final String name, final File... scripts) {
            this.schemas.add(SchemaConfig.aSchemaConfig(name).withScripts(scripts).build());
            return this;
        }

        public Builder addSchema(final String name, final List<File> scripts) {
            this.schemas.add(SchemaConfig.aSchemaConfig(name).withScripts(scripts).build());
            return this;
        }

        public Builder addSchema(final SchemaConfig config) {
            this.schemas.add(config);
            return this;
        }

        public EmbeddedMysql start() {
            EmbeddedMysql instance = new EmbeddedMysql(config);

            for (SchemaConfig schema : schemas) {
                instance.addSchema(schema);
            }

            return instance;
        }
    }
}

