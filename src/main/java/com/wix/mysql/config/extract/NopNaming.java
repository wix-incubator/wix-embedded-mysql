package com.wix.mysql.config.extract;

import de.flapdoodle.embed.process.extract.ITempNaming;

public class NopNaming implements ITempNaming {

    private final String basePath;

    public NopNaming() {
        this.basePath = "";
    }

    public NopNaming(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String nameFor(String prefix, String postfix) {
        return basePath + postfix;
    }
}
