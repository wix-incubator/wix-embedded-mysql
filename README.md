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
Api supports simplified building of environment and schemas

```scala
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver.classPathFile
import com.wix.mysql.distribution.Version.v5_6_latest

val mysqld = anEmbeddedMysql(v5_6_latest)
    .addSchema("aschema", classPathFile("db/001_init.sql"))
    .start

//do stuff
      
mysqld.stop // you do not have to explicitly stop mysql as it registers to a shutdown hook for that. 
```

Or builders in case you need more control of instance you are running and schemas you are creating

```scala
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver.classPathFiles
import com.wix.mysql.config.Charset.LATIN1
import com.wix.mysql.config.{SchemaConfig, MysqldConfig}
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import com.wix.mysql.distribution.Version.v5_6_latest

val config: MysqldConfig = aMysqldConfig(v5_6_latest)
  .withPort(1120)
  .withCharset(LATIN1)
  .withUser("someuser", "somepassword")
  .build

val schema: SchemaConfig = aSchemaConfig("aschema")
  .withScripts(classPathFiles("db/*.sql"))
  .build

val mysqld: EmbeddedMysql = anEmbeddedMysql(config)
  .addSchema(schema)
  .start

// do stuff

mysqld.stop

```

Additionally you can reset your schemas between tests

```scala
TBD
```

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
Use of this source code is governed by a [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0), which basically means you can use and modify it freely.
