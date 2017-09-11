package com.wix.mysql.distribution;

import com.wix.mysql.exceptions.UnsupportedPlatformException;
import com.wix.mysql.utils.Utils;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public enum Version implements IVersion {

    v5_5_40("5.5", 40, MacOsVersion.v10_6, Platform.Linux, Platform.OS_X),
    v5_5_50("5.5", 50, MacOsVersion.v10_9, Platform.Linux, Platform.OS_X),
    v5_5_51("5.5", 51, MacOsVersion.v10_9, Platform.Linux, Platform.OS_X),
    v5_5_52("5.5", 52, MacOsVersion.v10_9, Platform.Linux, Platform.OS_X),
    v5_5_latest(v5_5_52),
    v5_6_21("5.6", 21, MacOsVersion.v10_9),
    v5_6_22("5.6", 22, MacOsVersion.v10_9),
    v5_6_23("5.6", 23, MacOsVersion.v10_9),
    v5_6_24("5.6", 24, MacOsVersion.v10_9),
    v5_6_31("5.6", 31, MacOsVersion.v10_11),
    v5_6_32("5.6", 32, MacOsVersion.v10_11),
    v5_6_33("5.6", 33, MacOsVersion.v10_11),
    v5_6_34("5.6", 34, MacOsVersion.v10_11),
    v5_6_35("5.6", 35, MacOsVersion.v10_12),
    v5_6_36("5.6", 36, MacOsVersion.v10_12),
    v5_6_latest(v5_6_36),
    v5_7_10("5.7", 10, MacOsVersion.v10_10),
    v5_7_13("5.7", 13, MacOsVersion.v10_11),
    v5_7_14("5.7", 14, MacOsVersion.v10_11),
    v5_7_15("5.7", 15, MacOsVersion.v10_11),
    v5_7_16("5.7", 16, MacOsVersion.v10_11),
    v5_7_17("5.7", 17, MacOsVersion.v10_12),
    v5_7_18("5.7", 18, MacOsVersion.v10_12),
    v5_7_19("5.7", 19, MacOsVersion.v10_12),
    v5_7_latest(v5_7_19);

    private enum MacOsVersion {
        v10_6("osx"), v10_9("osx"), v10_10("osx"), v10_11("osx"),
        v10_12("macos");

        private final String osName;

        MacOsVersion(String osName) {
            this.osName = osName;
        }

        @Override
        public String toString() {
            return format("%s%s", osName, name().substring(1).replace('_', '.'));
        }
    }

    private final String majorVersion;
    private final int minorVersion;
    private final MacOsVersion macOsVersion;
    private final List<Platform> supportedPlatforms;

    Version(String majorVersion, int minorVersion, MacOsVersion macOsVersion, Platform... supportedPlatforms) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.macOsVersion = macOsVersion;
        this.supportedPlatforms = Arrays.asList(supportedPlatforms);
    }

    Version(String majorVersion, int minorVersion, MacOsVersion macOsVersion) {
        this(majorVersion, minorVersion, macOsVersion, Platform.Linux, Platform.Windows, Platform.OS_X);
    }

    Version(Version other) {
        this.majorVersion = other.majorVersion;
        this.minorVersion = other.minorVersion;
        this.macOsVersion = other.macOsVersion;
        this.supportedPlatforms = other.supportedPlatforms;
    }

    public boolean supportsCurrentPlatform() {
        return supportedPlatforms.contains(currentPlatform()) && (!isMacOsSierra() || worksOnMacOsSierra());
    }

    private boolean isMacOsSierra() {
        return currentPlatform() == Platform.OS_X && System.getProperty("os.version").startsWith("10.12");
    }

    private boolean worksOnMacOsSierra() {
        return currentPlatform() == Platform.OS_X && !majorVersion.equals("5.7") || minorVersion >= 15;
    }

    @Override
    public String asInDownloadPath() {
        assertPlatformIsSupported();

        switch (currentPlatform()) {
            case Windows:
                return format("/%s/mysql-%s.%s", path(), majorVersion, minorVersion);
            case OS_X:
                return format("/%s/mysql-%s.%s-%s", path(), majorVersion, minorVersion, macOsVersion);
            case Linux:
                return format("/%s/mysql-%s.%s-%s", path(), majorVersion, minorVersion, gcLibVersion());
            default:
                throw new UnsupportedPlatformException("Unrecognized platform, currently not supported");
        }
    }

    @Override
    public String toString() {
        return String.format("Version %s.%s", majorVersion, minorVersion);
    }

    private String gcLibVersion() {
        if(majorVersion.equals("5.7") && minorVersion > 18)
            return "linux-glibc2.12";
        if (majorVersion.equals("5.6") || (majorVersion.equals("5.7") && minorVersion <= 18))
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

    private String toVersionString() { return majorVersion + "." + minorVersion; }

    private void assertPlatformIsSupported() {
        if (isMacOsSierra() && !worksOnMacOsSierra()) {
            throw new UnsupportedPlatformException(String.format("%s is not supported on Mac OS Sierra. Minimum supported version is %s",
                    toString(),
                    v5_7_15.toVersionString()));
        }

        if (!supportsCurrentPlatform()) {
            throw new UnsupportedPlatformException(String.format("Platform %s is not in a supported platform list: %s",
                    currentPlatform().name(),
                    Utils.join(supportedPlatforms, ",")));
        }
    }

    public String getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }
}

