package com.wix.mysql;

import com.wix.mysql.distribution.Version;
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
                return buildNixFileSet((Version) distribution.getVersion());
        }
    }

    private FileSet buildWindowsFileSet() {

        //TODO: just wow! Ok, need to think of a way to extend flapdoodle process library to support folders instead of individual files;
        // and discuss with flapdoodle people;
        FileSet.Builder builder = FileSet.builder()
                .addEntry(Executable, "bin/mysqld.exe")
                .addEntry(Library, "bin/mysql.exe")
                .addEntry(Library, "bin/resolveip.exe")
                .addEntry(Library, "bin/mysqladmin.exe")
                .addEntry(Library, "share/english/errmsg.sys")
                .addEntry(Library, "data/test/db.opt")

                .addEntry(Library, "data/ib_logfile0")
                .addEntry(Library, "data/ib_logfile1")
                .addEntry(Library, "data/ibdata1")

                .addEntry(Library, "data/mysql/columns_priv.MYD")
                .addEntry(Library, "data/mysql/columns_priv.MYI")
                .addEntry(Library, "data/mysql/columns_priv.frm")
                .addEntry(Library, "data/mysql/db.MYD")
                .addEntry(Library, "data/mysql/db.MYI")
                .addEntry(Library, "data/mysql/db.frm")
                .addEntry(Library, "data/mysql/event.MYD")
                .addEntry(Library, "data/mysql/event.MYI")
                .addEntry(Library, "data/mysql/event.frm")
                .addEntry(Library, "data/mysql/func.MYD")
                .addEntry(Library, "data/mysql/func.MYI")
                .addEntry(Library, "data/mysql/func.frm")
                .addEntry(Library, "data/mysql/general_log.CSM")
                .addEntry(Library, "data/mysql/general_log.CSV")
                .addEntry(Library, "data/mysql/general_log.frm")
                .addEntry(Library, "data/mysql/help_category.MYD")
                .addEntry(Library, "data/mysql/help_category.MYI")
                .addEntry(Library, "data/mysql/help_category.frm")
                .addEntry(Library, "data/mysql/help_keyword.MYD")
                .addEntry(Library, "data/mysql/help_keyword.MYI")
                .addEntry(Library, "data/mysql/help_keyword.frm")
                .addEntry(Library, "data/mysql/help_relation.MYD")
                .addEntry(Library, "data/mysql/help_relation.MYI")
                .addEntry(Library, "data/mysql/help_relation.frm")
                .addEntry(Library, "data/mysql/help_topic.MYD")
                .addEntry(Library, "data/mysql/help_topic.MYI")
                .addEntry(Library, "data/mysql/help_topic.frm")
                .addEntry(Library, "data/mysql/innodb_index_stats.frm")
                .addEntry(Library, "data/mysql/innodb_index_stats.ibd")
                .addEntry(Library, "data/mysql/innodb_table_stats.frm")
                .addEntry(Library, "data/mysql/innodb_table_stats.ibd")
                .addEntry(Library, "data/mysql/ndb_binlog_index.MYD")
                .addEntry(Library, "data/mysql/ndb_binlog_index.MYI")
                .addEntry(Library, "data/mysql/ndb_binlog_index.frm")
                .addEntry(Library, "data/mysql/plugin.MYD")
                .addEntry(Library, "data/mysql/plugin.MYI")
                .addEntry(Library, "data/mysql/plugin.frm")
                .addEntry(Library, "data/mysql/proc.MYD")
                .addEntry(Library, "data/mysql/proc.MYI")
                .addEntry(Library, "data/mysql/proc.frm")
                .addEntry(Library, "data/mysql/procs_priv.MYD")
                .addEntry(Library, "data/mysql/procs_priv.MYI")
                .addEntry(Library, "data/mysql/procs_priv.frm")
                .addEntry(Library, "data/mysql/proxies_priv.MYD")
                .addEntry(Library, "data/mysql/proxies_priv.MYI")
                .addEntry(Library, "data/mysql/proxies_priv.frm")
                .addEntry(Library, "data/mysql/servers.MYD")
                .addEntry(Library, "data/mysql/servers.MYI")
                .addEntry(Library, "data/mysql/servers.frm")
                .addEntry(Library, "data/mysql/slave_master_info.frm")
                .addEntry(Library, "data/mysql/slave_master_info.ibd")
                .addEntry(Library, "data/mysql/slave_relay_log_info.frm")
                .addEntry(Library, "data/mysql/slave_relay_log_info.ibd")
                .addEntry(Library, "data/mysql/slave_worker_info.frm")
                .addEntry(Library, "data/mysql/slave_worker_info.ibd")
                .addEntry(Library, "data/mysql/slow_log.CSM")
                .addEntry(Library, "data/mysql/slow_log.CSV")
                .addEntry(Library, "data/mysql/slow_log.frm")
                .addEntry(Library, "data/mysql/tables_priv.MYD")
                .addEntry(Library, "data/mysql/tables_priv.MYI")
                .addEntry(Library, "data/mysql/tables_priv.frm")
                .addEntry(Library, "data/mysql/time_zone.MYD")
                .addEntry(Library, "data/mysql/time_zone.MYI")
                .addEntry(Library, "data/mysql/time_zone.frm")
                .addEntry(Library, "data/mysql/time_zone_leap_second.MYD")
                .addEntry(Library, "data/mysql/time_zone_leap_second.MYI")
                .addEntry(Library, "data/mysql/time_zone_leap_second.frm")
                .addEntry(Library, "data/mysql/time_zone_name.MYD")
                .addEntry(Library, "data/mysql/time_zone_name.MYI")
                .addEntry(Library, "data/mysql/time_zone_name.frm")
                .addEntry(Library, "data/mysql/time_zone_transition.MYD")
                .addEntry(Library, "data/mysql/time_zone_transition.MYI")
                .addEntry(Library, "data/mysql/time_zone_transition.frm")
                .addEntry(Library, "data/mysql/time_zone_transition_type.MYD")
                .addEntry(Library, "data/mysql/time_zone_transition_type.MYI")
                .addEntry(Library, "data/mysql/time_zone_transition_type.frm")
                .addEntry(Library, "data/mysql/user.MYD")
                .addEntry(Library, "data/mysql/user.MYI")
                .addEntry(Library, "data/mysql/user.frm")

                .addEntry(Library, "data/performance_schema/accounts.frm")
                .addEntry(Library, "data/performance_schema/cond_instances.frm")
                .addEntry(Library, "data/performance_schema/db.opt")
                .addEntry(Library, "data/performance_schema/events_stages_current.frm")
                .addEntry(Library, "data/performance_schema/events_stages_history.frm")
                .addEntry(Library, "data/performance_schema/events_stages_history_long.frm")
                .addEntry(Library, "data/performance_schema/events_stages_summary_by_account_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_stages_summary_by_host_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_stages_summary_by_thread_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_stages_summary_by_user_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_stages_summary_global_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_statements_current.frm")
                .addEntry(Library, "data/performance_schema/events_statements_history.frm")
                .addEntry(Library, "data/performance_schema/events_statements_history_long.frm")
                .addEntry(Library, "data/performance_schema/events_statements_summary_by_account_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_statements_summary_by_digest.frm")
                .addEntry(Library, "data/performance_schema/events_statements_summary_by_host_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_statements_summary_by_thread_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_statements_summary_by_user_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_statements_summary_global_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_waits_current.frm")
                .addEntry(Library, "data/performance_schema/events_waits_history.frm")
                .addEntry(Library, "data/performance_schema/events_waits_history_long.frm")
                .addEntry(Library, "data/performance_schema/events_waits_summary_by_account_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_waits_summary_by_host_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_waits_summary_by_instance.frm")
                .addEntry(Library, "data/performance_schema/events_waits_summary_by_thread_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_waits_summary_by_user_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/events_waits_summary_global_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/file_instances.frm")
                .addEntry(Library, "data/performance_schema/file_summary_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/file_summary_by_instance.frm")
                .addEntry(Library, "data/performance_schema/host_cache.frm")
                .addEntry(Library, "data/performance_schema/hosts.frm")
                .addEntry(Library, "data/performance_schema/mutex_instances.frm")
                .addEntry(Library, "data/performance_schema/objects_summary_global_by_type.frm")
                .addEntry(Library, "data/performance_schema/performance_timers.frm")
                .addEntry(Library, "data/performance_schema/rwlock_instances.frm")
                .addEntry(Library, "data/performance_schema/session_account_connect_attrs.frm")
                .addEntry(Library, "data/performance_schema/session_connect_attrs.frm")
                .addEntry(Library, "data/performance_schema/setup_actors.frm")
                .addEntry(Library, "data/performance_schema/setup_consumers.frm")
                .addEntry(Library, "data/performance_schema/setup_instruments.frm")
                .addEntry(Library, "data/performance_schema/setup_objects.frm")
                .addEntry(Library, "data/performance_schema/setup_timers.frm")
                .addEntry(Library, "data/performance_schema/socket_instances.frm")
                .addEntry(Library, "data/performance_schema/socket_summary_by_event_name.frm")
                .addEntry(Library, "data/performance_schema/socket_summary_by_instance.frm")
                .addEntry(Library, "data/performance_schema/table_io_waits_summary_by_index_usage.frm")
                .addEntry(Library, "data/performance_schema/table_io_waits_summary_by_table.frm")
                .addEntry(Library, "data/performance_schema/table_lock_waits_summary_by_table.frm")
                .addEntry(Library, "data/performance_schema/threads.frm")
                .addEntry(Library, "data/performance_schema/users.frm");


        //TODO: patch up process library to support multi-match pattern.
        //then we could just have regex and mark it as multi-match and no file-counting
        //as this one is dodgy especially when considering multiple versions etc.
        //for (int i = 0; i <= 3; i++)  { builder.addEntry(Library, "data/ib.*", "data/ib.*"); }
        //for (int i = 0; i <= 79; i++) { builder.addEntry(Library, "data/mysql/.*"); }
        //for (int i = 0; i <= 53; i++) { builder.addEntry(Library, "data/performance_schema/.*"); }

        return builder.build();
    }

    private FileSet buildNixFileSet(final Version version) {
        FileSet.Builder initial = FileSet.builder()
                .addEntry(Executable, "bin/mysqld")
                .addEntry(Library, "bin/mysql")
                .addEntry(Library, "bin/resolveip")
                .addEntry(Library, "bin/mysqladmin")
                .addEntry(Library, "bin/my_print_defaults")
                .addEntry(Library, "share/english/errmsg.sys")
                .addEntry(Library, "share/fill_help_tables.sql")
                .addEntry(Library, "share/mysql_system_tables.sql")
                .addEntry(Library, "share/mysql_system_tables_data.sql");


        if (!version.getMajorVersion().equals("5.5")) {
            initial.addEntry(Library, "share/mysql_security_commands.sql");//not available for 5.5
            initial.addEntry(Library, "support-files/my-default.cnf");
        }

        if (!version.getMajorVersion().equals("5.7")) {
            initial.addEntry(Library, "scripts/mysql_install_db");
        }

        return initial.build();
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
}
