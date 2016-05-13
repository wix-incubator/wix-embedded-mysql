package com.wix.mysql.distribution.service;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import com.wix.mysql.utils.Utils;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

public class CatchAllCommandEmitter implements CommandEmitter {

    @Override
    public boolean matches(Version version) {
        return true;
    }

    @Override
    public List<String> emit(final MysqldConfig config, final IExtractedFileSet exe) throws IOException {
        File baseDir = exe.baseDir();
        return Collections.newArrayList(
                exe.executable().getAbsolutePath(),
                "--no-defaults",
                "--log-output=NONE",
                format("--basedir=%s", baseDir),
                format("--datadir=%s/data", baseDir),
                format("--plugin-dir=%s/lib/plugin", baseDir),
                format("--lc-messages-dir=%s/share", baseDir),
                format("--port=%s", config.getPort()),
                "--console",
                format("--character-set-server=%s", config.getCharset().getCharset()),
                format("--collation-server=%s", config.getCharset().getCollate()),
                format("--default-time-zone=%s", Utils.asHHmmOffset(config.getTimeZone())));

    }
}
