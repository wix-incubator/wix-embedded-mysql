package com.wixpress.embed.mysql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.wixpress.embed.mysql.config.MysqldConfig;
import com.wixpress.embed.mysql.distribution.Version;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import static org.junit.Assert.*;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class RunnerTest {

    @Test
    public void allDefaults56() throws Exception {
        MysqldStarter starter = MysqldStarter.defaultInstance();

        MysqldConfig config = new MysqldConfig(Version.v5_6_21);
        MysqldExecutable executable = starter.prepare(config);
        try {
            MysqldProcess mysqld = executable.start();
            verifyConnection(defaultDataSource());
        } finally {
            executable.stop();
        }
    }

    @Test
    public void allDefaults55() throws Exception {
        MysqldStarter starter = MysqldStarter.defaultInstance();

        MysqldConfig config = new MysqldConfig(Version.v5_5_39);
        MysqldExecutable executable = starter.prepare(config);
        try {
            MysqldProcess mysqld = executable.start();
            verifyConnection(defaultDataSource());
        } finally {
            executable.stop();
        }
    }

    @Test
    public void customPort() throws Exception {
        MysqldStarter starter = MysqldStarter.defaultInstance();

        MysqldConfig config = new MysqldConfig(Version.v5_6_21, 3301);
        MysqldExecutable executable = starter.prepare(config);
        try {
            MysqldProcess mysqld = executable.start();
            verifyConnection(dataSourceFor("jdbc:mysql://localhost:3301/information_schema"));
        } finally {
            executable.stop();
        }
    }

    private void verifyConnection(DataSource dataSource) {
        long res = new JdbcTemplate(dataSource).queryForObject("select 1 from dual;", java.lang.Long.class);
        assertEquals(1, res);
    }

    private DataSource defaultDataSource() throws Exception {
        return dataSourceFor("jdbc:mysql://localhost:3306/information_schema");
    }

    private DataSource dataSourceFor(String url) throws Exception {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl(url);
        cpds.setUser("root");
        cpds.setPassword(null);
        return cpds;
    }
}
