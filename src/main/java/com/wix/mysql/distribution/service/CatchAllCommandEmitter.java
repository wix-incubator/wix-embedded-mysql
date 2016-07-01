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
                format("--socket=%s", sockFile(exe)),
                "--console",
                format("--character-set-server=%s", config.getCharset().getCharset()),
                format("--collation-server=%s", config.getCharset().getCollate()),
                format("--default-time-zone=%s", Utils.asHHmmOffset(config.getTimeZone())));

    }

    /**
     * Helper for getting stable sock classPathFile. Saving to local instance variable on service start does not work due
     * to the way flapdoodle process library works - it does all init in {@link de.flapdoodle.embed.process.runtime.AbstractProcess} and instance of
     * {@link com.wix.mysql.MysqldProcess} is not yet present, so vars are not initialized.
     * This algo gives stable sock classPathFile based on single executeCommands profile, but can leave trash sock classPathFiles in tmp dir.
     * <p>
     * Notes:
     * .sock classPathFile needs to be in system temp dir and not in ex. target/...
     * This is due to possible problems with existing mysql installation and apparmor profiles
     * in linuxes.
     */
    private String sockFile(IExtractedFileSet exe) throws IOException {
        String sysTempDir = System.getProperty("java.io.tmpdir");
        String sockFile = format("%s.sock", exe.baseDir().getName());
        return new File(sysTempDir, sockFile).getAbsolutePath();
    }
}
