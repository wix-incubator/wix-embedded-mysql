package com.wix.mysql.distribution;

import com.wix.mysql.exceptions.UnsupportedPlatformException;
import de.flapdoodle.embed.process.distribution.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static com.wix.mysql.distribution.Version.v5_5_40;
import static de.flapdoodle.embed.process.distribution.Platform.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class VersionTest {

    PlatformProvider platformProvider = Mockito.mock(PlatformProvider.class);
    Version v5_6_21 = Version.v5_6_21;


    @Test
    public void testOSXVersions() {
        givenPlatformSetTo(OS_X);

        assertThat(v5_6_21.asInDownloadPath(), is("MySQL-5.6/mysql-5.6.21-osx10.9"));
    }

    @Test
    public void testWindowsVersions() {
        givenPlatformSetTo(Windows);

        assertThat(v5_6_21.asInDownloadPath(), is("MySQL-5.6/mysql-5.6.21"));
    }

    @Test
    public void testLinuxVersions() {
        givenPlatformSetTo(Linux);

        v5_5_40.setPlatformProvider(platformProvider);

        assertThat(v5_5_40.asInDownloadPath(), is("MySQL-5.5/mysql-5.5.40-linux2.6"));
    }

    @Test
    public void testSolarisVersionsNotSupported() {
        givenPlatformSetTo(Solaris);

        exception.expect(UnsupportedPlatformException.class);
        assertThat(v5_6_21.asInDownloadPath(), is("MySQL-5.6/mysql-5.6.21"));
    }

    @Test
    public void testFreeBSDVersionsNotSupported() {
        givenPlatformSetTo(FreeBSD);

        exception.expect(UnsupportedPlatformException.class);
        assertThat(v5_6_21.asInDownloadPath(), is("MySQL-5.6/mysql-5.6.21"));
    }


    @Rule public ExpectedException exception = ExpectedException.none();

    @Before public void init() {
        v5_6_21.setPlatformProvider(platformProvider);
    }

    private void givenPlatformSetTo(Platform platform) {
        when(platformProvider.getPlatform()).thenReturn(platform);
    }

    @After public void cleanup() {
        v5_6_21.setPlatformProvider(new PlatformProvider());
        v5_5_40.setPlatformProvider(new PlatformProvider());
    }

}