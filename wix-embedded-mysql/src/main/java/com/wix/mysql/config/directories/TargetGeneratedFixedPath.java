package com.wix.mysql.config.directories;

import de.flapdoodle.embed.process.io.directories.IDirectory;

import java.io.File;
import java.util.UUID;

import static java.lang.String.format;

public class TargetGeneratedFixedPath implements IDirectory {

    private final String baseDir;

    public TargetGeneratedFixedPath(String prefix) {
        this.baseDir = format("target/%s-%s", prefix, UUID.randomUUID().toString());
    }

    @Override
    public File asFile() {
        File ret = new File(baseDir).getAbsoluteFile();
        if (!ret.exists()) {
            ret.mkdir();
        }
        return ret;
    }

    @Override
    public boolean isGenerated() {
        return true;
    }
}