package com.wix.mysql.distribution;

import de.flapdoodle.embed.process.distribution.Platform;

public class PlatformProvider {

    public Platform getPlatform() {
        return Platform.detect();
    }
}
