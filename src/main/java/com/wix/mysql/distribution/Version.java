package com.wix.mysql.distribution;

import com.wix.mysql.exceptions.UnsupportedPlatformException;
import com.wix.mysql.utils.Utils;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class Version implements IVersion {

    public static Version v5_5_40 = v5_5(40);
    public static Version v5_6_21 = v5_6(21);
    public static Version v5_6_22 = v5_6(22);
    public static Version v5_6_23 = v5_6(23);
    public static Version v5_6_24 = v5_7(24);
    public static Version v5_7_10 = v5_7(10);
    public static Version v5_7_13 = v5_7(13);
    public static Version v5_5_latest = v5_5_40;
    public static Version v5_6_latest = v5_6_24;
    public static Version v5_7_latest = v5_7_13;

    public static Version[] values() {
        return new Version[] {v5_5_latest, v5_6_latest, v5_7_latest};
    }

    public static Version v5_5(int minor) {
        return new Version("5.5", minor, Platform.Linux, Platform.OS_X);
    }

    public static Version v5_6(int minor) {
        return new Version("5.6", minor);
    }

    public static Version v5_7(int minor) {
        return new Version("5.7", minor);
    }

    private final String majorVersion;
    private final int minorVersion;
    private final List<Platform> supportedPlatforms;

    private Version(String majorVersion, int minorVersion, Platform... supportedPlatforms) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;

        if (supportedPlatforms.length > 0) {
            this.supportedPlatforms = Arrays.asList(supportedPlatforms);
        } else {
            this.supportedPlatforms = Arrays.asList(Platform.Linux, Platform.OS_X, Platform.Windows);
        }
    }

    public boolean supportsCurrentPlatform() {
        return supportedPlatforms.contains(currentPlatform());
    }

    @Override
    public String asInDownloadPath() {
        assertPlatformIsSupported();

        switch (currentPlatform()) {
            case Windows:
                return format("%s/mysql-%s.%s", path(), majorVersion, minorVersion);
            case OS_X:
                return format("%s/mysql-%s.%s-osx%s", path(), majorVersion, minorVersion, osVersion());
            case Linux:
                return format("%s/mysql-%s.%s-%s", path(), majorVersion, minorVersion, gcLibVersion());
            default:
                throw new UnsupportedPlatformException("Unrecognized platform, currently not supported");
        }
    }

    @Override
    public String toString() {
        return String.format("Version %s.%s", majorVersion, minorVersion);
    }

    private String osVersion() {
        if (majorVersion.equals("5.6") || majorVersion.equals("5.7"))
            //TODO: 5.6 has support for 10.8 as well. Maybe we should be smarter about it?
            return "10.9";
        if (majorVersion.equals("5.5"))
            return "10.6";
        throw new UnsupportedOperationException();
    }

    private String gcLibVersion() {
        if (majorVersion.equals("5.6") || majorVersion.equals("5.7"))
            return "linux-glibc2.5";
        if (majorVersion.equals("5.5"))
            return "linux2.6";
        throw new UnsupportedOperationException();
    }

    private Platform currentPlatform() {
        return Platform.detect();
    }

    private String path() {
        return format("MySQL-%s", majorVersion);
    }

    private void assertPlatformIsSupported() {
        if (!supportsCurrentPlatform()) {
            throw new UnsupportedPlatformException(String.format("Platform %s is not in a supported platform list: %s",
                    currentPlatform().name(),
                    Utils.join(supportedPlatforms, ",")));
        }
    }

    public String getMajorVersion() {
        return majorVersion;
    }
}
