package com.wix.mysql.distribution;

import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * @author viliusl
 * @since 27/09/14
 */
public enum Version implements IVersion {

    v5_6_21("5.6.21", "10.8"),
    v5_5_39("5.5.39", "10.6");

	private final String specificVersion;
    private final String osxVersion;

	Version(String vName, String osxVersion) {
        this.specificVersion = vName;
        this.osxVersion = osxVersion;
	}

    public String osXVersion() {
        return this.osxVersion;
    }

	@Override
	public String asInDownloadPath() {
		return specificVersion;
	}

	@Override
	public String toString() {
		return "Version{" + specificVersion + '}';
	}
}
