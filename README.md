This is an trial to build a sensible version of embedded mysql implementation for IT/unit tests using https://github.com/flapdoodle-oss/de.flapdoodle.embed.process library.

## Building/usage

For this to build you need to install https://github.com/wix/de.flapdoodle.embed.process into local maven repo - checkout and 'mvn clean install'

Sorry for inconvenience:/

## Some thoughts

 - Current flapdoodle impl does not have a notion of multiple files like main process file, scripts, support files (libs/.sql scripts etc.). For that purpose a fork of process lib has been created with initial short-cuts: https://github.com/wix/de.flapdoodle.embed.process.
 - Need to figure out a good way to enable multi-platform support/testing - vagrant?
 - Scala or java?
 - Current impl is a piece of sh**t to see if it's feasible and what amount of work is needed;
 - I would go with some different model for extracting/caching files than what main process library has, but we'll see. For simplicity sake current impl is ok;
 - then all test-framework, custom db, user, password etc will need to follow.

## Minimal file-set to have a simple working mysql server

This might be trimmed down on a file-by-file basis, but it's basically down from like 300M to 22M

```
.
├── bin
│   ├── my_print_defaults
│   ├── mysqladmin
│   └── mysqld
├── lib
│   └── plugin
│       ├── adt_null.so
│       ├── auth.so
│       ├── auth_test_plugin.so
│       ├── daemon_example.ini
│       ├── debug
│       │   ├── adt_null.so
│       │   ├── auth.so
│       │   ├── auth_test_plugin.so
│       │   ├── innodb_engine.so
│       │   ├── libdaemon_example.so
│       │   ├── libmemcached.so
│       │   ├── mypluglib.so
│       │   ├── qa_auth_client.so
│       │   ├── qa_auth_interface.so
│       │   ├── qa_auth_server.so
│       │   ├── semisync_master.so
│       │   ├── semisync_slave.so
│       │   └── validate_password.so
│       ├── innodb_engine.so
│       ├── libdaemon_example.so
│       ├── libmemcached.so
│       ├── mypluglib.so
│       ├── qa_auth_client.so
│       ├── qa_auth_interface.so
│       ├── qa_auth_server.so
│       ├── semisync_master.so
│       ├── semisync_slave.so
│       └── validate_password.so
├── scripts
│   └── mysql_install_db
├── share
│   ├── english
│   │   └── errmsg.sys
│   ├── fill_help_tables.sql
│   ├── mysql_security_commands.sql
│   ├── mysql_system_tables.sql
│   ├── mysql_system_tables_data.sql
│   └── mysql_test_data_timezone.sql
└── support-files
    ├── binary-configure
    ├── magic
    ├── my-default.cnf
    ├── mysql-log-rotate
    ├── mysql.server
    └── mysqld_multi.server
```

## Other platforms

Now development is being done on osx so osx and linux should be covered with generic packages, but windows might just have it's own gotchas. This might need to be tackled separately
