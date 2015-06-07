package com.wix.mysql;

import com.google.common.base.Strings;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfig.SystemDefaults;
import com.wix.mysql.exceptions.CommandFailedException;
import de.flapdoodle.embed.process.distribution.Platform;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static de.flapdoodle.embed.process.distribution.Platform.Windows;
import static java.lang.String.format;

/**
 * @author viliusl
 * @since 25/05/15
 */
class MysqlClient {

    private final MysqldConfig config;
    private final MysqldExecutable executable;
    private final String schemaName;

    public MysqlClient(final MysqldConfig config, final MysqldExecutable executable) {
        this(config, executable, SystemDefaults.SCHEMA);
    }

    public MysqlClient(final MysqldConfig config, final MysqldExecutable executable, final String schemaName) {
        this.config = config;
        this.executable = executable;
        this.schemaName = schemaName;
    }

    public void executeScripts(final List<File> files) {
        for (File file: files) {
            execute(format("source %s", file.getAbsolutePath()));
        }
    }

    public void executeScripts(final File... files) {
        for (File file: files) {
            execute(format("source %s", file.getAbsolutePath()));
        }
    }

    public void executeCommands(final List<String> sqls) {
        for (String sql: sqls) {
            execute(sql);
        }
    }

    public void executeCommands(final String... sqls) {
        for (String sql: sqls) {
            execute(sql);
        }
    }

    private void execute(final String sql) {
        String command = (Platform.detect() == Windows) ? format("\"%s\"", sql): sql;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                    Paths.get(executable.getBaseDir().getAbsolutePath(), "bin", "mysql").toString(),
                    "--protocol=tcp",
                    format("--user=%s", SystemDefaults.USERNAME),
                    format("--port=%s", config.getPort()),
                    "-e",
                    command,
                    schemaName});

            if (p.waitFor() != 0) {
                String out = IOUtils.toString(p.getInputStream());
                String err = IOUtils.toString(p.getErrorStream());

                if (Strings.isNullOrEmpty(out))
                    throw new CommandFailedException(command, schemaName, p.waitFor(), err);
                else
                    throw new CommandFailedException(command, schemaName, p.waitFor(), out);

            }

        } catch (IOException | InterruptedException e) {
            throw new CommandFailedException(command, schemaName, e.getMessage(), e);
        }
    }
}
