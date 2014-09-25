This is an trial to build a sensible version of embedded mysql implementation for IT/unit tests using https://github.com/flapdoodle-oss/de.flapdoodle.embed.process library.

## Building/usage

For this to build you need to install https://github.com/wix/de.flapdoodle.embed.process into local maven repo - checkout and 'mvn clean install'

Sorry for inconvenience:/

## TODO
 - submit a patch to flapdoodle process to get rid of a fork;
 - figure out if it should be in scala or java? I think java would be better in case of oss and maybe scala adapter/testing support libraries as an addition;
 - get rid of slf4j;
 - get rid of wix deps;
 - windows/linux;
 - figure out a way to test on different platforms - windows, linux, freebsd?
 - implement custom credentials support and custom db creation support?
 - test more cases with locales etc. Now version is really stripped-down, so something might be just missing;

## Some thoughts

 - Current flapdoodle impl does not have a notion of multiple files like main process file, scripts, support files (libs/.sql scripts etc.). For that purpose a fork of process lib has been created with initial short-cuts: https://github.com/wix/de.flapdoodle.embed.process.
 - Need to figure out a good way to enable multi-platform support/testing - vagrant?

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

## Other platforms

Now development is being done on osx so osx and linux should be covered with generic packages, but windows might just have it's own gotchas. This might need to be tackled separately
