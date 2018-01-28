package com.wix.mysql;

import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfig.SystemDefaults;
import com.wix.mysql.exceptions.CommandFailedException;
import de.flapdoodle.embed.process.distribution.Platform;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;

import static com.wix.mysql.utils.Utils.isNullOrEmpty;
import static de.flapdoodle.embed.process.distribution.Platform.Windows;
import static java.lang.String.format;

class MysqlClient {

    private final MysqldConfig config;
    private final MysqldExecutable executable;
    private final String schemaName;
    private final Charset effectiveCharset;

    public MysqlClient(
            final MysqldConfig config,
            final MysqldExecutable executable,
            final String schemaName,
            final Charset effectiveCharset) {
        this.config = config;
        this.executable = executable;
        this.schemaName = schemaName;
        this.effectiveCharset = effectiveCharset;
    }

    void executeScripts(final List<SqlScriptSource> sqls) {
        try {
            for (SqlScriptSource sql : sqls) {
                execute(sql.read());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeCommands(final String... sqls) {
        for (String sql : sqls) {
            execute(sql);
        }
    }

    private void execute(final String sql) {
        String command = (Platform.detect() == Windows) ? format("\"%s\"", sql) : sql;
        try {
            Process p = new ProcessBuilder(new String[]{
                    Paths.get(executable.getBaseDir().getAbsolutePath(), "bin", "mysql").toString(),
                    "--protocol=tcp",
                    "--host=localhost",
                    "--password=",
                    format("--default-character-set=%s", effectiveCharset.getCharset()),
                    format("--user=%s", SystemDefaults.USERNAME),
                    format("--port=%s", config.getPort()),
                    schemaName}).start();

            IOUtils.copy(new StringReader(sql), p.getOutputStream(), java.nio.charset.Charset.defaultCharset());
            p.getOutputStream().close();

            if (p.waitFor() != 0) {
                String out = IOUtils.toString(p.getInputStream());
                String err = IOUtils.toString(p.getErrorStream());

                if (isNullOrEmpty(out))
                    throw new CommandFailedException(command, schemaName, p.waitFor(), err);
                else
                    throw new CommandFailedException(command, schemaName, p.waitFor(), out);
            }

        } catch (IOException | InterruptedException e) {
            throw new CommandFailedException(command, schemaName, e.getMessage(), e);
        }
    }
}
