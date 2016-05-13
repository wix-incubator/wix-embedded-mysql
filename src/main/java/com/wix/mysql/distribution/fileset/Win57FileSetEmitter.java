package com.wix.mysql.distribution.fileset;

import com.wix.mysql.distribution.Version;
import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.distribution.Platform;

import java.util.Objects;

import static de.flapdoodle.embed.process.config.store.FileType.Executable;
import static de.flapdoodle.embed.process.config.store.FileType.Library;

public class Win57FileSetEmitter implements FileSetEmitter {
    @Override
    public boolean matches(Platform platform, Version version) {
        return !platform.isUnixLike() && Objects.equals(version.getMajorVersion(), "5.7");
    }

    @Override
    public FileSet emit() {
        return FileSet.builder()
                .addEntry(Executable, "bin/mysqld.exe")
                .addEntry(Library, "bin/echo.exe")
                .addEntry(Library, "bin/innochecksum.exe")
                .addEntry(Library, "bin/libmecab.dll")
                .addEntry(Library, "bin/lz4_decompress.exe")
                .addEntry(Library, "bin/msvcp120.dll")
                .addEntry(Library, "bin/msvcr120.dll")
                .addEntry(Library, "bin/my_print_defaults.exe")
                .addEntry(Library, "bin/myisam_ftdump.exe")
                .addEntry(Library, "bin/myisamchk.exe")
                .addEntry(Library, "bin/myisamlog.exe")
                .addEntry(Library, "bin/myisampack.exe")
                .addEntry(Library, "bin/mysql.exe")
                .addEntry(Library, "bin/mysql_client_test_embedded.exe")
                .addEntry(Library, "bin/mysql_config.pl")
                .addEntry(Library, "bin/mysql_config_editor.exe")
                .addEntry(Library, "bin/mysql_embedded.exe")
                .addEntry(Library, "bin/mysql_plugin.exe")
                .addEntry(Library, "bin/mysql_secure_installation.exe")
                .addEntry(Library, "bin/mysql_ssl_rsa_setup.exe")
                .addEntry(Library, "bin/mysql_tzinfo_to_sql.exe")
                .addEntry(Library, "bin/mysql_upgrade.exe")
                .addEntry(Library, "bin/mysqladmin.exe")
                .addEntry(Library, "bin/mysqlbinlog.exe")
                .addEntry(Library, "bin/mysqlcheck.exe")
                .addEntry(Library, "bin/mysqld.pdb")
                .addEntry(Library, "bin/mysqld_multi.pl")
                .addEntry(Library, "bin/mysqldump.exe")
                .addEntry(Library, "bin/mysqldumpslow.pl")
                .addEntry(Library, "bin/mysqlimport.exe")
                .addEntry(Library, "bin/mysqlpump.exe")
                .addEntry(Library, "bin/mysqlshow.exe")
                .addEntry(Library, "bin/mysqlslap.exe")
                .addEntry(Library, "bin/mysqltest_embedded.exe")
                .addEntry(Library, "bin/perror.exe")
                .addEntry(Library, "bin/replace.exe")
                .addEntry(Library, "bin/resolveip.exe")
                .addEntry(Library, "bin/zlib_decompress.exe")
                .addEntry(Library, "share/dictionary.txt")
                .addEntry(Library, "share/english/errmsg.sys")
                .addEntry(Library, "share/errmsg-utf8.txt")
                .addEntry(Library, "share/fill_help_tables.sql")
                .addEntry(Library, "share/innodb_memcached_config.sql")
                .addEntry(Library, "share/mysql_security_commands.sql")
                .addEntry(Library, "share/mysql_sys_schema.sql")
                .addEntry(Library, "share/mysql_system_tables.sql")
                .addEntry(Library, "share/mysql_system_tables_data.sql")
                .addEntry(Library, "share/mysql_test_data_timezone.sql")
                .build();
    }
}
