package com.wix.mysql;

import com.google.common.collect.Lists;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.RuntimeConfigBuilder;
import com.wix.mysql.config.SchemaConfig;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
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
    private final MysqldConfig config;
    private final String username;
    private final String password;
    private final MysqldExecutable executable;
    private final MysqldProcess process;
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    protected EmbeddedMysql(
            final MysqldConfig config,
            final String username,
            final String password) {
        this.config = config;
        this.username = username;
        this.password = password;
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults().build();
        this.executable = new MysqldStarter(runtimeConfig).prepare(config);

        try {
            this.process = executable.start(Distribution.detectFor(config.getVersion()), config, runtimeConfig);
            getClient().executeCommands(format("CREATE USER '%s'@'%%' IDENTIFIED BY '%s';", username, password));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MysqlClient getClient() {
        return new MysqlClient(config, executable);
    }

    private MysqlClient getClient(final SchemaConfig schema) {
        return new MysqlClient(config, executable, schema.getName());
    }

    public EmbeddedMysql addSchema(final SchemaConfig schema) {
        getClient().executeCommands(
                format("CREATE DATABASE %s CHARACTER SET = %s COLLATE = %s;",
                        schema.getName(), config.getCharset().getCharset(), config.getCharset().getCollate()),
                format("GRANT ALL ON %s.* TO '%s'@'%%';", schema.getName(), username));

        getClient(schema).executeScripts(schema.getScripts());

        return this;
    }

    public EmbeddedMysql apply(final SchemaConfig schema, File... files) {
        getClient(schema).executeScripts(files);
        return this;
    }

    public DataSource dataSourceFor(final SchemaConfig schema) {
        //TODO: reuse, create a provider
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(getJdbcConnectionUrl(schema));
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    public String getJdbcConnectionUrl(final SchemaConfig schema) {
        return format("jdbc:mysql://localhost:%s/%s", this.config.getPort(), schema.getName());
    }

    public synchronized void stop() {
        if (isRunning.getAndSet(false)) {
            process.stop();
            executable.stop();
        }
    }

    public static Builder Builder(final MysqldConfig config) {
        return new Builder(config);
    }

    public static Builder Builder(final Version version) {
        return new Builder(MysqldConfig.Builder(version).build());
    }

    public static Builder Builder(final Version version, final int port) {
        return new Builder(MysqldConfig.Builder(version).withPort(port).build());
    }

    public static class Builder {
        private final MysqldConfig config;
        private String username = "auser";
        private String password = "sa";
        private List<SchemaConfig> schemas = Lists.newArrayList();

        public Builder(final MysqldConfig config) {
            this.config = config;
        }

        public Builder withUser(final String username, final String password) {
            //TODO: make sure does not clash with system user/password
            this.username = username;
            this.password = password;
            return this;
        }

        public Builder addSchema(final SchemaConfig config) {
            this.schemas.add(config);
            return this;
        }

        public EmbeddedMysql start() {
            EmbeddedMysql instance = new EmbeddedMysql(config, username, password);

            for (SchemaConfig schema: schemas) {
                instance.addSchema(schema);
            }

            return instance;
        }
    }
}

