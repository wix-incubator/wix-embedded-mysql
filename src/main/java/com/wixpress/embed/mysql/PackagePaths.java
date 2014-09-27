package com.wixpress.embed.mysql;

import com.wixpress.embed.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
                .addEntry(FileType.Executable,  "bin/mysqld.exe")
                .addEntry(FileType.Script,      "bin/mysqladmin.exe")
                .addEntry(FileType.Support,     "share/english/errmsg.sys")
                .addEntry(FileType.Support,     "data/test/db.opt");

        //TODO: patch up process library to support multi-match pattern.
        //then we could just have regex and mark it as multi-match and no file-counting
        //as this one is dodgy especially when considering multiple versions etc.
        for (int i = 0; i <= 3; i++)  { builder.addEntry(FileType.Support, "data/ib.*"); }
        for (int i = 0; i <= 79; i++) { builder.addEntry(FileType.Support, "data/mysql/.*"); }
        for (int i = 0; i <= 53; i++) { builder.addEntry(FileType.Support, "data/performance_schema/.*"); }

        return builder.build();
    }

    private FileSet buildNixFileSet() {
        return FileSet.builder()
                .addEntry(FileType.Executable,  "bin/mysqld")
                .addEntry(FileType.Script,      "bin/mysqladmin")
                .addEntry(FileType.Script,      "bin/my_print_defaults")
                .addEntry(FileType.Script,      "scripts/mysql_install_db")
                .addEntry(FileType.Library,     "lib/plugin/innodb_engine.so")
                .addEntry(FileType.Support,     "share/english/errmsg.sys")
                .addEntry(FileType.Support,     "share/fill_help_tables.sql")
                .addEntry(FileType.Support,     "share/mysql_security_commands.sql")
                .addEntry(FileType.Support,     "share/mysql_system_tables.sql")
                .addEntry(FileType.Support,     "share/mysql_system_tables_data.sql")
                .addEntry(FileType.Support,     "support-files/my-default.cnf")
                .build();
    }


    @Override
    public ArchiveType getArchiveType(Distribution distribution) {
        switch (distribution.getPlatform()) {
            case Windows:
                return ArchiveType.ZIP;
            default:
                return ArchiveType.TGZ;
        }
    }

    @Override
    public String getPath(Distribution distribution) {
        Version v = (Version)distribution.getVersion();
        BitSize bs = distribution.getBitsize();
        switch (distribution.getPlatform()) {
            case OS_X:
              return String.format("mysql-%s-osx%s-x86%s.tar.gz",
                      v.asInDownloadPath(),
                      v.osXVersion(),
                      bs == BitSize.B32 ? "" : "_64");

            case Linux:
              return String.format("mysql-%s-linux-glibc2.5-%s.tar.gz",
                      v.asInDownloadPath(),
                      bs == BitSize.B32 ? "i686" : "x86_64");
            case Windows:
                return String.format("mysql-%s-win%s.zip",
                        v.asInDownloadPath(),
                        bs == BitSize.B32 ? "32" : "x64");
            default:
                throw new NotImplementedException();
        }
    }
}