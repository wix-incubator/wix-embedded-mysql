package com.wix.mysql;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.exceptions.CommandFailedException;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.io.Processors;

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
    private final IRuntimeConfig runtimeConfig;
    private final File generatedBaseDir;

    public MysqlConfigurer(MysqldConfig config, IRuntimeConfig runtimeConfig, File generatedBaseDir) {
        this.config = config;
        this.runtimeConfig = runtimeConfig;
        this.generatedBaseDir = generatedBaseDir;
    }

    public void configure() {
        List<String> commands = Lists.newArrayList();

        if (config.shouldCreateUser()) {
            commands.add(String.format("CREATE USER '%s'@'localhost' IDENTIFIED BY '%s';", config.getUsername(), config.getPassword()));
        }
        for (String schema : config.getSchemas()){
            commands.add(String.format("create database %s;", schema));
            commands.add(String.format("GRANT ALL ON %s.* TO '%s'@'localhost';", schema, config.getUsername()));
        }

        new MysqlCommandExecutor(generatedBaseDir, config, runtimeConfig).run(
                Joiner.on(" \n").join(commands),
                SystemDefaults.SCHEMA);
    }

    private static class MysqlCommandExecutor {

        private final File baseDir;
        private final MysqldConfig config;
        private final IRuntimeConfig runtimeConfig;

        public MysqlCommandExecutor(
                final File baseDir,
                final MysqldConfig config,
                final IRuntimeConfig runtimeConfig) {

            this.baseDir = baseDir;
            this.config = config;
            this.runtimeConfig = runtimeConfig;
        }

        public void run(String sqlStatement, String schema) {
            Reader stdOut = null;
            Reader stdErr = null;

            String statement = String.format((Platform.detect() == Windows) ? "\"%s\"" : "%s", sqlStatement);

            try {
                Process p = Runtime.getRuntime().exec(new String[] {
                        Paths.get(baseDir.getAbsolutePath(), "bin", "mysql").toString(),
                        "--protocol=tcp",
                        String.format("--user=%s", MysqldConfig.SystemDefaults.USERNAME),
                        String.format("--port=%s", config.getPort()),
                        "-e",
                        statement,
                        schema,
                        "--silent"});

                stdOut = new InputStreamReader(p.getInputStream());


                Processors.connect(stdOut, runtimeConfig.getProcessOutput().getOutput());

                if (p.waitFor() != 0) {
                    stdErr = new InputStreamReader(p.getErrorStream());
                    throw new CommandFailedException(sqlStatement, schema, p.waitFor(), CharStreams.toString(stdErr));
                }

            } catch (IOException | InterruptedException e) {
                throw new CommandFailedException(sqlStatement, schema, e.getMessage(), e);
            } finally {
                closeCloseables(stdOut, stdErr);
            }
        }
    }

}
