package com.wix.mysql.distribution;

import static java.lang.String.format;

import com.wix.mysql.exceptions.UnsupportedPlatformException;
import com.wix.mysql.utils.Utils;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;
import java.util.Arrays;
import java.util.List;

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
    v5_6_latest(v5_6_33),
    v5_7_10("5.7", 10, MacOsVersion.v10_10),
    v5_7_13("5.7", 13, MacOsVersion.v10_11),
    v5_7_14("5.7", 14, MacOsVersion.v10_11),
    v5_7_15("5.7", 15, MacOsVersion.v10_11),
    v5_7_latest(v5_7_15);

    private enum MacOsVersion {
        v10_6, v10_9, v10_10, v10_11;

        @Override
        public String toString() {
            return name().substring(1).replace('_', '.');
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

    public boolean supportDevXApi() {
        return !(majorVersion.equals("5.5") || majorVersion.equals("5.6")) &&
                (majorVersion.equals("5.7") && minorVersion >= 12);
    }

    private boolean isMacOsSierra() {
        return currentPlatform() == Platform.OS_X && System.getProperty("os.version").startsWith("10.12");
    }

    private boolean worksOnMacOsSierra() {
        return !majorVersion.equals("5.7") || minorVersion >= 15;
    }

    @Override
    public String asInDownloadPath() {
        assertPlatformIsSupported();

        switch (currentPlatform()) {
            case Windows:
                return format("%s/mysql-%s.%s", path(), majorVersion, minorVersion);
            case OS_X:
                return format("%s/mysql-%s.%s-osx%s", path(), majorVersion, minorVersion, macOsVersion);
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

    public int getMinorVersion() {
        return minorVersion;
    }
}
