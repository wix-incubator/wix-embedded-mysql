package com.wix.mysql.distribution.fileset;

import de.flapdoodle.embed.process.config.store.FileSet;

import static de.flapdoodle.embed.process.config.store.FileType.Executable;
import static de.flapdoodle.embed.process.config.store.FileType.Library;

class Nix {
    FileSet.Builder common() {
        return FileSet.builder()
                .addEntry(Executable, "bin/mysqld")
                .addEntry(Library, "bin/mysql")
                .addEntry(Library, "bin/resolveip")
                .addEntry(Library, "bin/mysqladmin")
                .addEntry(Library, "bin/my_print_defaults")
                .addEntry(Library, "share/english/errmsg.sys")
                .addEntry(Library, "share/fill_help_tables.sql")
                .addEntry(Library, "share/mysql_system_tables.sql")
                .addEntry(Library, "share/mysql_system_tables_data.sql");
    }
}
