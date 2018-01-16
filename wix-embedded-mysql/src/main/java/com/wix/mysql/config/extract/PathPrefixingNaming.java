package com.wix.mysql.config.extract;

import de.flapdoodle.embed.process.extract.ITempNaming;

public class PathPrefixingNaming implements ITempNaming {

    private final String basePath;

    public PathPrefixingNaming(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String nameFor(String prefix, String postfix) {
        return basePath + postfix;
    }
}
