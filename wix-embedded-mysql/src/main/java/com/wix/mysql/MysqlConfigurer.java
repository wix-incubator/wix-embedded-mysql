package com.wix.mysql;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.wix.mysql.config.MysqldConfig;

import java.util.List;

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

        new MysqlClient(config, executable).apply(Joiner.on(" \n").join(commands));
    }
}
