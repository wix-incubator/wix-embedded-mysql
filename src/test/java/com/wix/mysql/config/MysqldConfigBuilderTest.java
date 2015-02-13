package com.wix.mysql.config;

import com.wix.mysql.distribution.Version;
import org.junit.Test;

import static com.wix.mysql.config.MysqldConfig.Defaults;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author viliusl
 * @since 10/12/14
 */
public class MysqldConfigBuilderTest {

    private final MysqldConfigBuilder template = new MysqldConfigBuilder(Version.v5_6_21);

    @Test
    public void populateWithDefaultsWhenNoValuesProvided() {
        MysqldConfig config = template.build();

        assertThat(config.getUsername(), equalTo(Defaults.USERNAME));
        assertThat(config.getPassword(), equalTo(Defaults.PASSWORD));
        assertThat(config.getSchema(), equalTo(Defaults.SCHEMA));
        assertThat(config.getPort(), equalTo(Defaults.PORT));
    }

    @Test
    public void customUsername() {
        MysqldConfig config = template.withUsername(Defaults.USERNAME + "a").build();
        assertThat(config.getUsername(), equalTo(Defaults.USERNAME + "a"));
    }

    @Test
    public void customPassword() {
        MysqldConfig config = template.withPassword(Defaults.PASSWORD + "a").build();
        assertThat(config.getPassword(), equalTo(Defaults.PASSWORD + "a"));
    }

    @Test
    public void customSchema() {
        MysqldConfig config = template.withSchema(Defaults.SCHEMA + "a").build();
        assertThat(config.getSchema(), equalTo(Defaults.SCHEMA + "a"));
    }

    @Test
    public void customPort() {
        MysqldConfig config = template.withPort(Defaults.PORT + 1).build();
        assertThat(config.getPort(), equalTo(Defaults.PORT + 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void failOnNullUsername() {
        MysqldConfig config = template.withUsername(null).build();
    }

    @Test
    public void allowNullPassword() {
        MysqldConfig config = template.withPassword(null).build();
        assertThat(config.getPassword(), is(nullValue()));
    }

}
