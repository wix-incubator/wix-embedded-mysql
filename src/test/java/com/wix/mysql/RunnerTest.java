package com.wix.mysql;

import org.junit.Test;

import static com.wix.mysql.distribution.Version.v5_5_39;
import static com.wix.mysql.distribution.Version.v5_6_21;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class RunnerTest extends EmbeddedMySqlTestSupport {


    @Test
    public void runMySql_5_6_onDefaultPort() throws Exception {
        MysqldExecutable executable = givenMySqlWithVersion(v5_6_21);
        try {
            executable.start();
            verifyDBIsStartedOn(3306);
        } finally {
            executable.stop();
        }
    }

    @Test
    public void runMySql_5_5_onDefaultPort() throws Exception {
        MysqldExecutable executable = givenMySqlWithVersion(v5_5_39);
        try {
            executable.start();
            verifyDBIsStartedOn(3306);
        } finally {
            executable.stop();
        }
    }

    @Test
    public void runMySql_5_6_onCustomPort() throws Exception {
        MysqldExecutable executable = givenMySqlWithVersionAndPort(v5_6_21, 3301);
        try {
            executable.start();

            verifyDBIsStartedOn(3301);
        } finally {
            executable.stop();
        }
    }

}

