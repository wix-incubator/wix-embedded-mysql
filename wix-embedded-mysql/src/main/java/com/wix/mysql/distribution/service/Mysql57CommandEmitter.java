package com.wix.mysql.distribution.service;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.IOException;
import java.util.List;

public class Mysql57CommandEmitter implements CommandEmitter {
    @Override
    public boolean matches(Version version) {
        return version.getMajorVersion().equals("5.7");
    }

    @Override
    public List<String> emit(MysqldConfig config, IExtractedFileSet exe) throws IOException {
        return Collections.newArrayList("--show_compatibility_56=ON", "--log_syslog=0");
    }
}
