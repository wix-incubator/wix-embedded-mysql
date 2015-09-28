package com.wix.mysql.config.extract;

import de.flapdoodle.embed.process.extract.ITempNaming;

public class NoopNaming implements ITempNaming {

    @Override
    public String nameFor(String prefix, String postfix) {
        return postfix;
    }
}
