package com.wixpress.embed.mysql.runtime;

import java.io.File;
import java.util.List;

import com.wixpress.embed.mysql.config.MysqldConfig;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

/**
 * @author viliusl
 * @since 18/09/14
 */
public class Mysqld {

    public static List<String> getCommandLine(
            final MysqldConfig config,
            final IExtractedFileSet extractedFiles,
            final File pidFile) {

        final String baseDir = extractedFiles.generatedBaseDir().getAbsolutePath();

        return Collections.newArrayList(
            extractedFiles.executable().getAbsolutePath(),
            String.format("--basedir=%s", baseDir),
            String.format("--datadir==%s/data", baseDir),
            String.format("--plugin-dir==%s/lib/plugin", baseDir),
            String.format("--pid-file==%s", pidFile.getAbsolutePath()),
            String.format("--port==%s", config.port()),
            "--console");//windows specific
    }

}
