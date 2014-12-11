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
public class SupportedVersionRunnerTest extends EmbeddedMySqlTestSupport {

    @Test
    public void runMySql_5_5_Defaults() throws Exception {
        MysqldConfig config = new MysqldConfigBuilder(v5_5_40).build();

        startAndVerifyDatabase(config);
    }

    @Test
    public void runMySql_5_6_Defaults() throws Exception {
        MysqldConfig config = new MysqldConfigBuilder(v5_6_21).build();

        startAndVerifyDatabase(config);
    }
}

