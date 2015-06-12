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
            case OS_X:
                return buildOSXFileSet();
            default:
                return buildNixFileSet();
        }
    }

    private FileSet buildOSXFileSet() {
        return aFileSetBuilder("bin/mysqld", "bin/mysql", "bin/resolveip", "bin/mysqladmin", "bin/my_print_defaults",
                "scripts/mysql_install_db", "lib/plugin/innodb_engine.so", "share/english/errmsg.sys",
                "share/fill_help_tables.sql", "share/mysql_security_commands.sql", "share/mysql_system_tables.sql",
                "share/mysql_system_tables_data.sql", "support-files/my-default.cnf")
                .build();
    }

    private FileSet buildWindowsFileSet() {
        //TODO: data folder added as somewhat hack.
        return aFileSetBuilder("bin/mysqld.exe", "bin/mysql.exe", "bin/resolveip.exe", "bin/mysqladmin.exe",
                "share/english/errmsg.sys")
                .addEntry(Library, "data", "FOLDER")
                .build();
    }

    private FileSet buildNixFileSet() {
        return aFileSetBuilder("bin/mysqld", "bin/mysql", "bin/resolveip", "bin/mysqladmin", "bin/my_print_defaults",
                "scripts/mysql_install_db", "lib/plugin/innodb_engine.so", "lib/plugin/auth_socket.so", "share/english/errmsg.sys",
                "share/fill_help_tables.sql", "share/mysql_security_commands.sql", "share/mysql_system_tables.sql",
                "share/mysql_system_tables_data.sql", "support-files/my-default.cnf")
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
                throw new RuntimeException("Not implemented for: " + distribution.getPlatform());
        }
    }

    private FileSet.Builder aFileSetBuilder(String exec, String... libraries) {
        FileSet.Builder builder = FileSet.builder().addEntry(Executable, exec);
        for (String name : libraries) {
            builder.addEntry(Library, name);
        }
        return builder;
    }
}