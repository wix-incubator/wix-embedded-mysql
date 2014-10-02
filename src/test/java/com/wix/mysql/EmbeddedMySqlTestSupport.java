package com.wix.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.wix.mysql.MysqldExecutable;
import com.wix.mysql.MysqldStarter;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

public abstract class EmbeddedMySqlTestSupport {

    public MysqldExecutable givenMySqlWithVersion(Version version) {
        return givenMySqlWithVersionAndPort(version, 3306);
    }

    public MysqldExecutable givenMySqlWithVersionAndPort(Version version, int port) {
        MysqldStarter starter = MysqldStarter.defaultInstance();

        MysqldConfig config = new MysqldConfig(version, port);
        return starter.prepare(config);
    }

    private DataSource dataSourceFor(String url) throws Exception {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl(url);
        cpds.setUser("root");
        cpds.setPassword(null);
        return cpds;
    }

    private String connectionUrlOn(int port) {
        return String.format("jdbc:mysql://localhost:%s/information_schema", port);
    }

    public void verifyDBIsStartedOn(int port) throws Exception {
        long res = new JdbcTemplate(dataSourceFor(connectionUrlOn(port))).queryForObject("select 1;", Long.class);
        assertEquals(1, res);
    }

}
