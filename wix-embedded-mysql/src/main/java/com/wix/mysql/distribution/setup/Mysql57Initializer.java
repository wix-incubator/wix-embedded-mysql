package com.wix.mysql.distribution.setup;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class Mysql57Initializer implements Initializer {
    @Override
    public boolean matches(Version version) {
        return version.getMajorVersion().equals("5.7");
    }

    @Override
    public void apply(IExtractedFileSet files, IRuntimeConfig runtimeConfig, MysqldConfig config) throws IOException {
        File baseDir = files.baseDir();
        // TODO: wait until https://github.com/flapdoodle-oss/de.flapdoodle.embed.process/pull/41 is merged
        FileUtils.deleteDirectory(new File(baseDir, "data"));

        Process p = Runtime.getRuntime().exec(new String[] {
                        files.executable().getAbsolutePath(),
                        "--no-defaults",
                        "--initialize-insecure",
                        "--ignore-db-dir",
                        format("--basedir=%s", baseDir),
                        format("--datadir=%s/data", baseDir)});

        new ProcessRunner(files.executable().getAbsolutePath()).run(p, runtimeConfig, config.getTimeout(NANOSECONDS));
    }
}
