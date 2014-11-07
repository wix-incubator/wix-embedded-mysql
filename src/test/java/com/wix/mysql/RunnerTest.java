package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfigBuilder;
import org.junit.Test;

import static com.wix.mysql.distribution.Version.v5_5_40;
import static com.wix.mysql.distribution.Version.v5_6_21;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class RunnerTest extends EmbeddedMySqlTestSupport {

    @Test
    public void runMySql_5_5_Defaults() throws Exception {
        MysqldConfig config = new MysqldConfigBuilder(v5_5_40).withPort(9911).build();
        MysqldExecutable executable = givenMySqlWithConfig(config);
        try {
            executable.start();
            verifyDBIsStartedFor(config);
        } finally {
            executable.stop();
        }
    }

    @Test
    public void runMySql_5_6_Defaults() throws Exception {
        MysqldConfig config = new MysqldConfigBuilder(v5_6_21).withPort(9912).build();
        MysqldExecutable executable = givenMySqlWithConfig(config);
        try {
            executable.start();
            verifyDBIsStartedFor(config);
        } finally {
            executable.stop();
        }
    }

    @Test
    public void runMySql_5_6_withCustomConfig() throws Exception {
        MysqldConfig config = new MysqldConfigBuilder(v5_6_21)
                .withUsername("auser")
                .withPassword("sa")
                .withSchema("some_schema")
                .withPort(9913).build();

        MysqldExecutable executable = givenMySqlWithConfig(config);
        try {
            executable.start();
            verifyDBIsStartedFor(config);
        } finally {
            executable.stop();
        }
    }


}

