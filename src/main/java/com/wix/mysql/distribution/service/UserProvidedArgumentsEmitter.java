package com.wix.mysql.distribution.service;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.IOException;
import java.util.List;

public class UserProvidedArgumentsEmitter implements CommandEmitter {
    @Override
    public boolean matches(Version version) {
        return true;
    }

    @Override
    public List<String> emit(MysqldConfig config, IExtractedFileSet exe) throws IOException {
        return config.getArgs();
    }
}
