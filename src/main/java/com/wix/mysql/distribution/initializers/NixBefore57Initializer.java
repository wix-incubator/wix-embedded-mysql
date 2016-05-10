package com.wix.mysql.distribution.initializers;

import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

public class NixBefore57Initializer implements Initializer {
    @Override
    public boolean matches(Version version) {
        return Platform.detect().isUnixLike() &&
                (version.getMajorVersion().equals("5.6") || version.getMajorVersion().equals("5.5"));
    }

    @Override
    public void apply(IExtractedFileSet files) throws IOException {
        File baseDir = files.baseDir();
        Process p = Runtime.getRuntime().exec(new String[]{
                        "scripts/mysql_install_db",
                        "--no-defaults",
                        format("--basedir=%s", baseDir),
                        format("--datadir=%s/data", baseDir)},
                null,
                baseDir);

        ProcessRunner.run(p);
    }
}
