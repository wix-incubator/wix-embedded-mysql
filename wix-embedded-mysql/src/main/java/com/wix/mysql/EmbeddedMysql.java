package com.wix.mysql;

import com.google.common.collect.Lists;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.RuntimeConfigBuilder;
import com.wix.mysql.config.SchemaConfig;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;

/**
 * @author viliusl
 * @since 07/06/15
 */
public class EmbeddedMysql {
    protected final MysqldConfig config;
    protected final MysqldExecutable executable;
    protected final MysqldProcess process;
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    protected EmbeddedMysql(final MysqldConfig config) {
        this.config = config;
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults().build();
        this.executable = new MysqldStarter(runtimeConfig).prepare(config);

        try {
            this.process = executable.start(Distribution.detectFor(config.getVersion()), config, runtimeConfig);
            getClient().executeCommands(
                    format("CREATE USER '%s'@'%%' IDENTIFIED BY '%s';", config.getUsername(), config.getPassword()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MysqldConfig getConfig() { return this.config; }

    private MysqlClient getClient() {
        return new MysqlClient(config, executable);
    }

    private MysqlClient getClient(final String schemaName) {
        return new MysqlClient(config, executable, schemaName);
    }

    public EmbeddedMysql addSchema(final String schemaName, final File... scripts) {
        return addSchema(SchemaConfig.aSchemaConfig(schemaName).withScripts(scripts).build());
    }

    public EmbeddedMysql addSchema(final SchemaConfig schema) {
        getClient().executeCommands(
                format("CREATE DATABASE %s CHARACTER SET = %s COLLATE = %s;",
                        schema.getName(), schema.getCharset().getCharset(), schema.getCharset().getCollate()),
                format("GRANT ALL ON %s.* TO '%s'@'%%';", schema.getName(), config.getUsername()));

        getClient(schema.getName()).executeScripts(schema.getScripts());

        return this;
    }

    public synchronized void stop() {
        if (isRunning.getAndSet(false)) {
            process.stop();
            executable.stop();
        }
    }

    public static Builder anEmbeddedMysql(final Version version) {
        return new Builder(MysqldConfig.aMysqldConfig(version).build());
    }

    public static Builder anEmbeddedMysql(final MysqldConfig config) {
        return new Builder(config);
    }

    public static class Builder {
        private final MysqldConfig config;
        private List<SchemaConfig> schemas = Lists.newArrayList();

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

            for (SchemaConfig schema: schemas) {
                instance.addSchema(schema);
            }

            return instance;
        }
    }
}

