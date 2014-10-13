package com.wix.mysql.config.directories;

import de.flapdoodle.embed.process.io.directories.IDirectory;

import java.io.File;
import java.util.UUID;

public class TargetGeneratedFixedPath implements IDirectory {

    private final String prefix;
    private final String baseDir;

    public TargetGeneratedFixedPath(String prefix) {
        this.prefix = prefix;
        this.baseDir = "target/" + prefix + "-"+ UUID.randomUUID().toString();
    }

    @Override
    public File asFile() {
        generateNeededDirs();

        return new File(baseDir).getAbsoluteFile();
    }

    private void generateNeededDirs() {
        for (String dir: new String[]{ "bin", "scripts", "lib/plugin", "share/english", "share", "support-files" } ) {
            new File(baseDir + "/" + dir).mkdirs();
        }
    }

    @Override
    public boolean isGenerated() {
        return true;
    }
}

