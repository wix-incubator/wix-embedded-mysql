# Wix Embedded MySql [![Build Status (Travis: Linux/OSX)](https://img.shields.io/travis/wix/wix-embedded-mysql/master.svg?label=linux%2FOSX%20build)](https://travis-ci.org/wix/wix-embedded-mysql) [![Build Status (AppVeyor: Windows)](https://img.shields.io/appveyor/ci/viliusl/wix-embedded-mysql/master.svg?label=windows%20build)](https://ci.appveyor.com/project/viliusl/wix-embedded-mysql) [![Maven Central](https://img.shields.io/maven-central/v/com.wix/wix-embedded-mysql.svg)](http://mvnrepository.com/artifact/com.wix/wix-embedded-mysql)

Wix Embedded MySql library provides a way to run **real** MySql within your integration tests.

## Why?
- Your tests can run on production-like environment: match version, encoding, timezone, database/schema/user settings;
- Its easy, much easier than installing right version by hand;
- You can use different versions/configuration per project without any local set-up;
- Supports multiple platforms: Windows, Linux and OSX;
- Provides constantly updated multiple versions of MySql - 5.5, 5.6, 5.7;
- Testing matrix for all supported OSes (x86/x64) and versions (5.5, 5.6, 5.7).

#Usage
## Maven
Add dependency to your pom.xml:

```xml
    <dependency>
        <groupId>com.wix</groupId>
        <artifactId>wix-embedded-mysql</artifactId>
        <version>x.y.z</version>
        <scope>test</scope>
    </dependency>
```

## Examples

You can start an embedded mysql with defaults and a single schema:

```java
import com.wix.mysql.EmbeddedMysql;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.distribution.Version.v5_6_latest;

EmbeddedMysql mysqld = anEmbeddedMysql(v5_6_latest)
    .addSchema("aschema", classPathScript("db/001_init.sql"))
    .start();

//do work

mysqld.stop(); //optional, as there is a shutdown hook
```

If you need more control in configuring embeded mysql instance, you can use MysqlConfig builder:

```java
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.EmbeddedMysql;
import static com.wix.mysql.ScriptResolver;

import java.util.concurrent.TimeUnit;

import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.distribution.Version.v5_6_23;
import static com.wix.mysql.config.Charset.UTF8;

MysqldConfig config = aMysqldConfig(v5_6_23)
    .withCharset(UTF8)
    .withPort(2215)
    .withUser("differentUser", "anotherPassword")
    .withTimeZone("Europe/Vilnius")
    .withTimeout(2, TimeUnit.MINUTES)
    .withServerVariable("max_connect_errors", 666)
    .build();

EmbeddedMysql mysqld = anEmbeddedMysql(config)
    .addSchema("aschema", ScriptResolver.classPathScript("db/001_init.sql"))
    .addSchema("aschema2", ScriptResolver.classPathScripts("db/*.sql"))
    .start();

//do work

mysqld.stop(); //optional, as there is a shutdown hook
```

EmbeddedMysql supports multiple schemas and additional configuration options provided via SchemaConfig builder:

```java
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.SchemaConfig;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.ScriptResolver.classPathScripts;
import static com.wix.mysql.config.SchemaConfig.aSchemaConfig;
import static com.wix.mysql.config.Charset.LATIN1;
import static com.wix.mysql.distribution.Version.v5_6_latest;

SchemaConfig schema = aSchemaConfig("aSchema")
    .withScripts(classPathScript("db/001_init.sql"))
    .withCharset(LATIN1)
    .build();

EmbeddedMysql mysqld = anEmbeddedMysql(v5_6_latest)
    .addSchema(schema)
    .addSchema("aschema2", classPathScripts("db/*.sql"))
    .start();

//do work

mysqld.stop(); //optional, as there is a shutdown hook
```

It is intended to be started once per test-suite, but you can reset schema in between tests which recreates database and applies provided migrations:

```java
import com.wix.mysql.EmbeddedMysql;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathScript;
import static com.wix.mysql.distribution.Version.v5_6_latest;

EmbeddedMysql mysqld = anEmbeddedMysql(v5_6_latest)
    .addSchema("aschema", classPathScript("db/001_init.sql"))
    .start();

//do work

mysqld.reloadSchema("aschema", classPathScript("db/001_init.sql"));

//continue on doing work

mysqld.stop(); //optional, as there is a shutdown hook
```

Source for examples can be found [here](https://github.com/wix/wix-embedded-mysql/blob/master/src/test/scala/com/wix/mysql/JavaUsageExamplesTest.java)

# Dependencies
Build on top of [de.flapdoodle.embed.process](https://github.com/flapdoodle-oss/de.flapdoodle.embed.process)

# How it works
 - After detecting current platform and requested version, Wix Embedded MySql will download the correct version from [dev.mysql.com](http://dev.mysql.com/get/Downloads/) and extract needed files to local folder. Note that this is a **one-time** action, where subsequent invocations use pre-downloaded/pre-extracted cached package.
 - Upon execution needed files are being copied into **target** folder, database created, service started and post-configuration (user, schema, etc.) performed.
 - On jvm shutdown mysqld process is stopped and temporary files cleaned-up.

# Tested on
 - latest OSX;
 - ubuntu precise 32/64;
 - windows 2012;

# Known issues
 - mysql, starting version 5.5.10 requires `libaio1.so` to be preinstalled on linux, but that is not the case for some linux distributions. Proper error is emitted if it's missing and you have to install it manually (ex. ubuntu):
```bash
sudo apt-get install libaio1
```

#License
Use of this source code is governed by a [BSD License](LICENSE.md), which basically means you can use and modify it freely.
