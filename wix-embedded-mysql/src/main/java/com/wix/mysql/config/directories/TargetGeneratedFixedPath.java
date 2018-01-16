package com.wix.mysql.config.directories;

import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.io.directories.IDirectory;

import java.io.File;

public class TargetGeneratedFixedPath implements IDirectory {

    private final String baseDir;

    public TargetGeneratedFixedPath(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public File asFile() {
        generateNeededDirs();
        return new File(baseDir).getAbsoluteFile();
    }

    private void generateNeededDirs() {
        String[] paths;

        if (Platform.detect() == Platform.Windows ) {
            paths = new String[]{"bin", "share/english", "data/test", "data/mysql", "data/performance_schema"};
        } else {
            paths = new String[]{"bin", "scripts", "lib/plugin", "share/english", "share", "support-files"};
        }

        for (String dir : paths) {
            new File(baseDir + "/" + dir).mkdirs();
        }
    }

    @Override
    public boolean isGenerated() {
        return true;
    }
}