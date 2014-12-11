package com.wix.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfigBuilder;
import com.wix.mysql.distribution.Version;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

public abstract class EmbeddedMySqlTestSupport {

    protected MysqldExecutable givenMySqlWithConfig(final MysqldConfig config) {
        MysqldStarter starter = MysqldStarter.defaultInstance();
        return starter.prepare(config);
    }

    protected void startAndVerifyDatabase(final MysqldConfig config) {
        MysqldExecutable executable = givenMySqlWithConfig(config);
        try {
            executable.start();
            long res = new JdbcTemplate(dataSourceFor(config)).queryForObject("select 1;", Long.class);
            assertEquals(1, res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executable.stop();
        }
    }

    private DataSource dataSourceFor(MysqldConfig config) throws Exception {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl(connectionUrlFor(config));
        cpds.setUser(config.getUsername());
        cpds.setPassword(config.getPassword());
        return cpds;
    }

    private String connectionUrlFor(MysqldConfig config) {
        return String.format("jdbc:mysql://localhost:%s/%s", config.getPort(), config.getSchema());
    }


}
