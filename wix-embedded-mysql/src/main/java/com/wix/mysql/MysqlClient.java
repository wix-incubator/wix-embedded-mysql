package com.wix.mysql;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.MysqldConfig.SystemDefaults;
import com.wix.mysql.exceptions.CommandFailedException;
import de.flapdoodle.embed.process.distribution.Platform;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Paths;

import static de.flapdoodle.embed.process.distribution.Platform.Windows;

/**
 * @author viliusl
 * @since 25/05/15
 */
class MysqlClient {

    private final MysqldConfig config;
    private final MysqldExecutable executable;

    public MysqlClient(final MysqldConfig config, final MysqldExecutable executable) {
        this.config = config;
        this.executable = executable;
    }

    public void apply(final String... sqls) {
        apply(Joiner.on("\n").join(sqls));
    }

    public void apply(String sql) {
        String statement = String.format((Platform.detect() == Windows) ? "\"%s\"" : "%s", sql);

        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                    Paths.get(executable.getBaseDir().getAbsolutePath(), "bin", "mysql").toString(),
                    "--protocol=tcp",
                    String.format("--user=%s", SystemDefaults.USERNAME),
                    String.format("--port=%s", config.getPort()),
                    "-e",
                    statement,
                    SystemDefaults.SCHEMA});

            if (p.waitFor() != 0) {
                String out = IOUtils.toString(p.getInputStream());
                String err = IOUtils.toString(p.getErrorStream());

                if (Strings.isNullOrEmpty(out))
                    throw new CommandFailedException(sql, SystemDefaults.SCHEMA, p.waitFor(), err);
                else
                    throw new CommandFailedException(sql, SystemDefaults.SCHEMA, p.waitFor(), out);

            }

        } catch (IOException | InterruptedException e) {
            throw new CommandFailedException(sql, SystemDefaults.SCHEMA, e.getMessage(), e);
        }
    }
}
