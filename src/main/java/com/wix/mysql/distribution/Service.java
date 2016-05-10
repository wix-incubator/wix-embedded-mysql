package com.wix.mysql.distribution;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.service.CatchAllCommandEmitter;
import com.wix.mysql.distribution.service.CommandEmitter;
import com.wix.mysql.distribution.service.Mysql57CommandEmitter;
import com.wix.mysql.distribution.service.Pre57CommandEmitter;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.IOException;
import java.util.List;

public class Service {

    private static List<CommandEmitter> emitters = Collections.newArrayList(
            new Pre57CommandEmitter(),
            new Mysql57CommandEmitter(),
            new CatchAllCommandEmitter());

    public static List<String> commandLine(final MysqldConfig config, final IExtractedFileSet exe) throws IOException {
        for (CommandEmitter emitter : emitters) {
            if (emitter.matches(config.getVersion())) {
                return emitter.emit(config, exe);
            }
        }

        throw new RuntimeException("Emitter not found for version: " + config.getVersion().toString());
    }
}