package com.wix.mysql;

import com.google.common.collect.Lists;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfigBuilder;
import com.wix.mysql.distribution.Version;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * @author viliusl
 * @since 27/09/14
 */
@RunWith(Parameterized.class)
public class SupportedVersionRunnerTest extends EmbeddedMySqlTestSupport {

    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog();

    private final Version version;

    public SupportedVersionRunnerTest(final Version version) {
        this.version = version;
    }

    @Parameters(name = "{0}")
    public static List<Version[]> versions() {
        List<Version[]> result = Lists.newArrayList();
        for (Version version : Version.values()) {
            if (version.supportsCurrentPlatform())
                result.add(new Version[] {version});
        }
        return result;
    }

    @Test
    public void defaults() throws Exception {
        MysqldConfig config = new MysqldConfigBuilder(version).build();

        startAndVerifyDatabase(config);

        assertFalse(
            "Failed to shutdown properly, found message 'Something bad...' in output",
            log.getLog().contains("Something bad happend."));
    }
}

