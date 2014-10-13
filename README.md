This is an trial to build a sensible version of embedded mysql implementation for IT/unit tests using https://github.com/flapdoodle-oss/de.flapdoodle.embed.process library.

## Problems
 - ubuntu precise linux32/64 fails due to missing libaio1.so - 'apt-get install libaio1' fixed this. Guess we should leave it for devs to make sure deps are in proper shape on their machines.
 - windows pops-up firewall window - will need to find a way to come around this;

## TODO
 - implement custom credentials support and custom db creation support?
 - test more cases with locales etc. Now version is really stripped-down, so something might be just missing;
 
 
## Minimal file-set to have a simple working mysql server

Minimal for linux:

```
.
├── bin
│   ├── my_print_defaults
│   ├── mysqladmin
│   └── mysqld
├── lib
│   └── plugin
│       └── innodb_engine.so
├── scripts
│   └── mysql_install_db
├── share
│   ├── english
│   │   └── errmsg.sys
│   ├── fill_help_tables.sql
│   ├── mysql_security_commands.sql
│   ├── mysql_system_tables.sql
│   └── mysql_system_tables_data.sql
└── support-files
    └── my-default.cnf
```
