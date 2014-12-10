package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfigBuilder;
import org.junit.Test;

import static com.wix.mysql.distribution.Version.v5_6_21;

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
    public void runMySqlWithSystemUserAndCustomSchema() throws Exception {
        MysqldConfig config = template
                .withUsername(MysqldConfig.SystemDefaults.USERNAME)
                .withPassword(MysqldConfig.SystemDefaults.PASSWORD)
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

