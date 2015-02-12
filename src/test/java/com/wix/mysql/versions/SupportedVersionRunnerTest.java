package com.wix.mysql.versions;

import com.google.common.collect.Lists;
import com.wix.mysql.EmbeddedMySqlTestSupport;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfigBuilder;
import com.wix.mysql.distribution.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.List;

/**
 * @author viliusl
 * @since 27/09/14
 */
@RunWith(Parameterized.class)
public class SupportedVersionRunnerTest extends EmbeddedMySqlTestSupport {

    @Parameters(name = "{0}")
    public static List<Version[]> versions() {
        List<Version[]> result = Lists.newArrayList();
        for (Version version : Version.values()) {
            result.add(new Version[] {version});
        }
        return result;
    }

    private final Version version;

    public SupportedVersionRunnerTest(final Version version) {
        this.version = version;
    }

    @Test
    public void defaults() throws Exception {
        MysqldConfig config = new MysqldConfigBuilder(version).build();
        startAndVerifyDatabase(config);
    }
}

