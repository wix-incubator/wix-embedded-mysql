package com.wix.mysql.distribution;

import com.wix.mysql.exceptions.UnsupportedPlatformException;
import de.flapdoodle.embed.process.distribution.IVersion;

import static java.lang.String.format;

/**
 * @author viliusl
 * @since 27/09/14
 */
public enum Version implements IVersion {

    v5_6_21("5.6", "21"),
    v5_5_40("5.5", "40");

	private final String majorVersion;
	private final String minorVersion;
	private PlatformProvider platformProvider = new PlatformProvider();

    Version(String majorVersion, String minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }


    protected void setPlatformProvider(PlatformProvider platformProvider) {
        this.platformProvider = platformProvider;
    }

    private String osVersion() {
        if (majorVersion.equals("5.6"))
            return "10.9";
        if (majorVersion.equals("5.5"))
            return "10.6";
        throw new UnsupportedOperationException();
    }

    private String gcLibVersion() {
        if (majorVersion.equals("5.6"))
            return "linux-glibc2.5";
        if (majorVersion.equals("5.5"))
            return "linux2.6";
        throw new UnsupportedOperationException();
    }

    private String path() {
        return format("MySQL-%s", majorVersion);
    }


	@Override
	public String asInDownloadPath() {
        switch (platformProvider.getPlatform()) {
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
		return "Version{" + majorVersion + '}';
	}
}
