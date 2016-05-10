package com.wix.mysql.distribution.service;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.IOException;
import java.util.List;

public interface CommandEmitter {
    boolean matches(final Version version);

    List<String> emit(final MysqldConfig config, final IExtractedFileSet exe) throws IOException;
}
