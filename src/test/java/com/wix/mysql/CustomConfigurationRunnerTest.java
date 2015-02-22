package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfigBuilder;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.concurrent.Callable;

import static com.wix.mysql.config.MysqldConfig.SystemDefaults.*;
import static com.wix.mysql.distribution.Version.v5_6_21;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class CustomConfigurationRunnerTest extends EmbeddedMySqlTestSupport {

    private final MysqldConfigBuilder template = new MysqldConfigBuilder(v5_6_21);

    @Test
    public void runMySql_5_6_Defaults() throws Exception {
        MysqldConfig config = template.build();

        startAndVerifyDatabase(config);
    }

    @Test
    public void runMySqlWithCustomConfig() throws Exception {
        MysqldConfig config = template
                .withUsername("auser")
                .withPassword("sa")
                .withSchema("some_schema")
                .withPort(9913).build();

        startAndVerifyDatabase(config);
    }

    @Test
    public void runMySqlWithTwoSchemas() throws Exception {
        final MysqldConfig config = template
                .withUsername("auser")
                .withPassword("sa")
                .withSchemas(new String[]{"schema1", "schema2"})
                .withPort(9913).build();

        startAndVerify(config, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                List<String> dbs = new JdbcTemplate(dataSourceFor(config, SCHEMA)).queryForList("SHOW DATABASES;", String.class);
                assertThat(dbs, hasItems("schema1", "schema2"));
                return null;
            }
        });
    }

    @Test
    public void runMySqlWithSystemUserAndCustomSchema() throws Exception {
        MysqldConfig config = template
                .withUsername(USERNAME)
                .withPassword(PASSWORD)
                .withSchema(MysqldConfig.Defaults.SCHEMA + "a")
                .build();

        startAndVerifyDatabase(config);
    }

    @Test
    public void runMySqlWithNullPassword() throws Exception {
        MysqldConfig config = template.withPassword(null).build();

        startAndVerifyDatabase(config);
    }

}

