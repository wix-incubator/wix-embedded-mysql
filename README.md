# Wix Embedded MySql [![Build Status (Travis: Linux)](https://img.shields.io/travis/wix/wix-embedded-mysql.svg?label=linux%20build)](https://travis-ci.org/wix/wix-embedded-mysql) [![Build Status (AppVeyor: Windows)](https://img.shields.io/appveyor/ci/viliusl/wix-embedded-mysql-ppwc6.svg?label=windows%20build)](https://ci.appveyor.com/project/viliusl/wix-embedded-mysql-ppwc6)

Wix Embedded MySql library provides an easy to use MySql for tests.

## Why?
- Your tests can run on production-like environment: match version, encoding, database/schema/user settings;
- Its easy, much easier than installing right version by hand;
- You can use different versions/configuration per project without any local set-up;
- Supports multiple platforms: Windows, Linux and OSX.

#Usage
## Maven
Add dependency to your pom.xml:

```xml
    <dependency>
        <groupId>com.wix</groupId>
        <artifactId>wix-embedded-mysql</artifactId>
        <version>1.0.0</version>
        <scope>test</scope>
    </dependency>        
```

## Examples

You can start and embedded mysql with defaults and a single schema

```java
    EmbeddedMysql mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .start();

    //do work

    mysqld.stop(); //optional, as there is a shutdown hook
```

If you need more control in configuring embeded mysql instance, you can use MysqlConfig builder 

```java
    MysqldConfig config = aMysqldConfig(v5_6_23)
        .withCharset(UTF8)
        .withPort(2215)
        .withUser("differentUser", "anotherPasword")
        .build();

    EmbeddedMysql mysqld = anEmbeddedMysql(config)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .addSchema("aschema2", classPathFiles("db/*.sql"))
        .start();

    //do work

    mysqld.stop(); //optional, as there is a shutdown hook
```

EmbeddedMysql supports multiple schemas and additional configuration options provided via SchemaConfig builder

```java
    SchemaConfig schema = aSchemaConfig("aSchema")
        .withScripts(classPathFile("db/001_init.sql"))
        .withCharset(LATIN1)
        .build();

    EmbeddedMysql mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema(schema)
        .addSchema("aschema2", classPathFiles("db/*.sql"))
        .start();

    //do work

    mysqld.stop(); //optional, as there is a shutdown hook
```

EmbeddedMysql is intended to be started once per test-suite, but you can reset schema in between tests which recreates database and applies provided migrations

```java
    EmbeddedMysql mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .start();

    //do work

    mysqld.reloadSchema("aschema", classPathFile("db/001_init.sql"));

    //continue on doing work

    mysqld.stop(); //optional, as there is a shutdown hook
```

Source for examples can be found [here](https://github.com/wix/wix-embedded-mysql/blob/master/wix-embedded-mysql/src/test/scala/com/wix/mysql/JavaUsageExamplesTest.java)

# Dependencies
Build on top of embed Process Util [de.flapdoodle.embed.process](https://github.com/flapdoodle-oss/de.flapdoodle.embed.process)

# How it works
 - After detecting current platform and requested version, Wix Embedded MySql will download the correct version from [MySql Repo](http://dev.mysql.com/get/Downloads/). Note that this is a **one-time** action, where subsequent invocations use pre-downloaded cached package.
 - Upon execution needed files are being extracted into **target** folder, database created, service started and post-configuration (user, schema, etc.) performed.
 - On jvm shutdown mysqld process is stopped and temporary files cleaned-up.

# Tested on
 - latest osx;
 - ubuntu precise 32/64;
 - windows 7;

# Known issues
 - some linux distros does not have libaio1.so which is a requirement by latest version of mysql. Proper error is emitted if it's missing;

#License
Use of this source code is governed by a [MIT License](LICENSE.md), which basically means you can use and modify it freely.
