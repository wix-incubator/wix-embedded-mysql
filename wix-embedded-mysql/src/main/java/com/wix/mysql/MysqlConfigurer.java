package com.wix.mysql;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.exceptions.CommandFailedException;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.io.Processors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.List;

import static com.wix.mysql.config.MysqldConfig.SystemDefaults;
import static com.wix.mysql.utils.Utils.closeCloseables;
import static de.flapdoodle.embed.process.distribution.Platform.Windows;

/**
 * @author viliusl
 * @since 06/11/14
 */
public class MysqlConfigurer {

    private final MysqldConfig config;
    private final MysqldExecutable executable;

    public MysqlConfigurer(final MysqldConfig config, final MysqldExecutable executable) {
        this.config = config;
        this.executable = executable;
    }

    public void configure() {
        List<String> commands = Lists.newArrayList();

        if (config.shouldCreateUser()) {
            commands.add(String.format("CREATE USER '%s'@'%%' IDENTIFIED BY '%s';", config.getUsername(), config.getPassword()));
        }
        for (String schema : config.getSchemas()) {
            commands.add(String.format("create database %s;", schema));
            commands.add(String.format("GRANT ALL ON %s.* TO '%s'@'%%';", schema, config.getUsername()));
        }

        new Mysql(config, executable).apply(Joiner.on(" \n").join(commands));
    }
}
