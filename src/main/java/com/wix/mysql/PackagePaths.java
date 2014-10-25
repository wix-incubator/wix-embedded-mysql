package com.wix.mysql;

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;

import static de.flapdoodle.embed.process.config.store.FileType.Executable;
import static de.flapdoodle.embed.process.config.store.FileType.Library;
import static de.flapdoodle.embed.process.distribution.ArchiveType.TGZ;
import static de.flapdoodle.embed.process.distribution.ArchiveType.ZIP;
import static de.flapdoodle.embed.process.distribution.BitSize.B32;
import static java.lang.String.format;

/**
 * @author viliusl
 * @since 27/09/14
 */
public class PackagePaths implements IPackageResolver {
    @Override
    public FileSet getFileSet(Distribution distribution) {
        switch (distribution.getPlatform()) {
            case Windows:
                return buildWindowsFileSet();
            default:
                return buildNixFileSet();
        }
    }

    private FileSet buildWindowsFileSet() {
        FileSet.Builder builder = FileSet.builder()
                .addEntry(Executable, "bin/mysqld.exe")
                .addEntry(Library,    "bin/resolveip.exe")
                .addEntry(Library,    "bin/mysqladmin.exe")
                .addEntry(Library,    "share/english/errmsg.sys")
                .addEntry(Library,    "data/test/db.opt");

        //TODO: patch up process library to support multi-match pattern.
        //then we could just have regex and mark it as multi-match and no file-counting
        //as this one is dodgy especially when considering multiple versions etc.
        for (int i = 0; i <= 3; i++)  { builder.addEntry(Library, "data/ib.*"); }
        for (int i = 0; i <= 79; i++) { builder.addEntry(Library, "data/mysql/.*"); }
        for (int i = 0; i <= 53; i++) { builder.addEntry(Library, "data/performance_schema/.*"); }

        return builder.build();
    }

    private FileSet buildNixFileSet() {
        return FileSet.builder()
                .addEntry(Executable, "bin/mysqld")
                .addEntry(Library,    "bin/resolveip")
                .addEntry(Library,    "bin/mysqladmin")
                .addEntry(Library,    "bin/my_print_defaults")
                .addEntry(Library,    "scripts/mysql_install_db")
                .addEntry(Library,    "lib/plugin/innodb_engine.so")
                .addEntry(Library,    "lib/plugin/auth_socket.so")
                .addEntry(Library,    "share/english/errmsg.sys")
                .addEntry(Library,    "share/fill_help_tables.sql")
                .addEntry(Library,    "share/mysql_security_commands.sql")
                .addEntry(Library,    "share/mysql_system_tables.sql")
                .addEntry(Library,    "share/mysql_system_tables_data.sql")
                .addEntry(Library,    "support-files/my-default.cnf")
                .build();
    }


    @Override
    public ArchiveType getArchiveType(Distribution distribution) {
        switch (distribution.getPlatform()) {
            case Windows:
                return ZIP;
            default:
                return TGZ;
        }
    }

    @Override
    public String getPath(Distribution distribution) {
        IVersion v = distribution.getVersion();
        BitSize bs = distribution.getBitsize();
        switch (distribution.getPlatform()) {
            case OS_X:
              return format("%s-x86%s.tar.gz",
                      v.asInDownloadPath(),
                      bs == B32 ? "" : "_64");
            case Linux:
              return format("%s-%s.tar.gz",
                      v.asInDownloadPath(),
                      bs == B32 ? "i686" : "x86_64");
            case Windows:
                return format("%s-win%s.zip",
                        v.asInDownloadPath(),
                        bs == B32 ? "32" : "x64");
            default:
                throw new RuntimeException("Not implemented");
        }
    }
}