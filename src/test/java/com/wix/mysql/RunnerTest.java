package com.wix.mysql;

import org.junit.Test;

import static com.wix.mysql.distribution.Version.v5_5_40;
import static com.wix.mysql.distribution.Version.v5_6_21;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class RunnerTest extends EmbeddedMySqlTestSupport {

    @Test
    public void runMySql_5_5_onCustomPort() throws Exception {
        MysqldExecutable executable = givenMySqlWithVersionAndPort(v5_5_40, 9911);
        try {
            executable.start();
            verifyDBIsStartedOn(9911);
        } finally {
            executable.stop();
        }
    }

    @Test
    public void runMySql_5_6_onCustomPort() throws Exception {
        MysqldExecutable executable = givenMySqlWithVersionAndPort(v5_6_21, 9912);
        try {
            executable.start();

            verifyDBIsStartedOn(9912);
        } finally {
            executable.stop();
        }
    }

}

