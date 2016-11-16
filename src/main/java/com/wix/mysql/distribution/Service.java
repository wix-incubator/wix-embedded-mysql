package com.wix.mysql.distribution;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.service.*;
import de.flapdoodle.embed.process.collections.Collections;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.IOException;
import java.util.List;

public class Service {

    private static List<CommandEmitter> emitters = Collections.newArrayList(
            new BaseCommandEmitter(),
            new Pre57CommandEmitter(),
            new Mysql57CommandEmitter());

    public static List<String> commandLine(final MysqldConfig config, final IExtractedFileSet exe) throws IOException {
        ServiceCommandBuilder commandBuilder = new ServiceCommandBuilder(config.getVersion().toString());
        for (CommandEmitter emitter : emitters) {
            if (emitter.matches(config.getVersion())) {
                commandBuilder.addAll(emitter.emit(config, exe));
            }
        }

        return commandBuilder.emit();
    }
}