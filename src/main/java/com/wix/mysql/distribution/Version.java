package com.wix.mysql.distribution;

import com.wix.mysql.exceptions.UnsupportedPlatformException;
import com.wix.mysql.utils.Utils;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * @author viliusl
 * @since 27/09/14
 */
public enum Version implements IVersion {

    v5_5_40("5.5", "40", Platform.Linux, Platform.OS_X),
    v5_6_21("5.6", "21"),
    v5_6_22("5.6", "22"),
    v5_6_23("5.6", "23"),
    v5_6_24("5.6", "24"),
    v5_6_latest(v5_6_24),
    v5_7_10("5.7", "10"),
    v5_7_latest(v5_7_10);

    private final String majorVersion;
    private final String minorVersion;
    private final List<Platform> supportedPlatforms;

    Version(String majorVersion, String minorVersion, Platform... supportedPlatforms) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.supportedPlatforms = Arrays.asList(supportedPlatforms);
    }

    Version(String majorVersion, String minorVersion) {
        this(majorVersion, minorVersion, Platform.Linux, Platform.Windows, Platform.OS_X);
    }

    Version(Version other) {
        this.majorVersion = other.majorVersion;
        this.minorVersion = other.minorVersion;
        this.supportedPlatforms = other.supportedPlatforms;
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
