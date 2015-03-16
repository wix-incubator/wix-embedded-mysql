package com.wix.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.wix.mysql.common.LoggingAdapter;
import com.wix.mysql.config.MysqldConfig;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

public abstract class EmbeddedMySqlTestSupport {

    static {
        LoggingAdapter.initLogging();
    }

    protected MysqldExecutable givenMySqlWithConfig(final MysqldConfig config) {
        MysqldStarter starter = MysqldStarter.defaultInstance();
        return starter.prepare(config);
    }

    protected void startAndVerifyDatabase(final MysqldConfig config) {
        startAndVerify(config, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (String schema: config.getSchemas()){
                    validateConnection(config, schema);
                }
                return null;
            }
        });
    }

    protected void startAndVerify(final MysqldConfig config, Callable<Void> verify) {
        MysqldExecutable executable = givenMySqlWithConfig(config);
        try {
            executable.start();
            verify.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executable.stop();
        }
    }

    private void validateConnection(MysqldConfig config, String schema) throws Exception {
        long res = new JdbcTemplate(dataSourceFor(config, schema)).queryForObject("select 1;", Long.class);
        assertEquals(1, res);
    }

    protected DataSource dataSourceFor(MysqldConfig config, String schema) throws Exception {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl(connectionUrlFor(config, schema));
        cpds.setUser(config.getUsername());
        cpds.setPassword(config.getPassword());
        return cpds;
    }

    private String connectionUrlFor(MysqldConfig config, String schema) {
        return String.format("jdbc:mysql://localhost:%s/%s", config.getPort(), schema);
    }
}
