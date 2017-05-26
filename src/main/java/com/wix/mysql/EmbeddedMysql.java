package com.wix.mysql;

import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfig.SystemDefaults;
import com.wix.mysql.config.RuntimeConfigBuilder;
import com.wix.mysql.config.SchemaConfig;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static com.wix.mysql.config.MysqldConfig.SystemDefaults.SCHEMA;
import static com.wix.mysql.utils.Utils.or;
import static java.lang.String.format;

public class EmbeddedMysql {
    private final static Logger logger = LoggerFactory.getLogger(EmbeddedMysql.class);
    private static final ReentrantLock localRepository = new ReentrantLock();

    protected final MysqldConfig config;
    protected final MysqldExecutable executable;
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    protected EmbeddedMysql(final MysqldConfig config) {
        logger.info("Preparing EmbeddedMysql version '{}'...", config.getVersion());
        this.config = config;
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults()
                .withConfig(config)
                .build();
        MysqldStarter mysqldStarter = new MysqldStarter(runtimeConfig);

        localRepository.lock();
        try {
            this.executable = mysqldStarter.prepare(config);
        } finally {
            localRepository.unlock();
        }

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

    public void reloadSchema(final String schemaName, final SqlScriptSource... scripts) {
        reloadSchema(SchemaConfig.aSchemaConfig(schemaName).withScripts(scripts).build());
    }

    public void reloadSchema(final String schemaName, final List<SqlScriptSource> scripts) {
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

        public Builder addSchema(final String name, final SqlScriptSource... scripts) {
            this.schemas.add(SchemaConfig.aSchemaConfig(name).withScripts(scripts).build());
            return this;
        }

        public Builder addSchema(final String name, final List<SqlScriptSource> scripts) {
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

