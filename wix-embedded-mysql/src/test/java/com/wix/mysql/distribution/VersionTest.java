package com.wix.mysql.distribution;

import com.wix.mysql.exceptions.UnsupportedPlatformException;
import de.flapdoodle.embed.process.distribution.Platform;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import static com.wix.mysql.distribution.Version.v5_5_40;
import static com.wix.mysql.distribution.Version.v5_6_21;
import static de.flapdoodle.embed.process.distribution.Platform.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VersionTest {

    @Rule
    public final RestoreSystemProperties restore = new RestoreSystemProperties();

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
        assertThat(v5_5_40.asInDownloadPath(), is("MySQL-5.5/mysql-5.5.40-linux2.6"));
    }

    @Test(expected = UnsupportedPlatformException.class)
    public void testWindowsFor55VersionNotSupported() {
        givenPlatformSetTo(Windows);
        v5_5_40.asInDownloadPath();
    }

    @Test(expected = UnsupportedPlatformException.class)
    public void testSolarisVersionsNotSupported() {
        givenPlatformSetTo(Solaris);
        v5_6_21.asInDownloadPath();
    }

    @Test(expected = UnsupportedPlatformException.class)
    public void testFreeBSDVersionsNotSupported() {
        givenPlatformSetTo(FreeBSD);
        v5_6_21.asInDownloadPath();
    }

    private void givenPlatformSetTo(Platform platform) {
        switch (platform) {
            case Windows:
                System.setProperty("os.name", "Windows");
                break;
            case OS_X:
                System.setProperty("os.name", "Mac OS X");
                break;
            case Linux:
                System.setProperty("os.name", "Linux");
                break;
            case Solaris:
                System.setProperty("os.name", "SunOS");
                break;
            case FreeBSD:
                System.setProperty("os.name", "FreeBSD");
                break;
            default:
                throw new UnsupportedPlatformException("Unrecognized platform, currently not supported");
        }
    }
}