# Wix Embedded MySql [![Build Status (Travis: Linux)](https://img.shields.io/travis/wix/wix-embedded-mysql.svg?label=linux%20build)](https://travis-ci.org/wix/wix-embedded-mysql) [![Build Status (AppVeyor: Windows)](https://img.shields.io/appveyor/ci/viliusl/wix-embedded-mysql-ppwc6.svg?label=windows%20build)](https://ci.appveyor.com/project/viliusl/wix-embedded-mysql-ppwc6)

Wix Embedded MySql library provides an easy to use MySql for tests.

## Why?

- its easy, much easier as installing right version by hand
- you can change version per test
- Support multiple platforms: Windows, Linux and OSX


# How it works

 - After detecting current platform and requested version, Wix Embedded MySql will download the correct version from [MySql Repo](http://dev.mysql.com/get/Downloads/). Note that this is a **one-time** action, where subsequent invocations use pre-downloaded cached package.
 - Upon execution needed files are being extracted into **target** folder, database created, service started and post-configuration (user, schema, etc.) performed.
 - On jvm shutdown mysqld process is stopped and temporary files cleaned-up.

#Usage

## Basic usage example

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

## Providing custom schemas, credentials, port

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

You can of course override defaults by providing custom values for the builder with the following guidelines:
 - You of course cannot provide null/empty user or schema;
 - You cannot use schema defined in 'com.wix.mysql.config.MysqldConfig.SystemDefaults';
 - You can use system credentials, but you cannot use system user and custom password.

### Maven

Add dependency to your pom.xml:

```xml
    <dependency>
        <groupId>com.wixpress.mysql</groupId>
        <artifactId>wix-embedded-mysql</artifactId>
        <version>1.1.0</version>
        <scope>test</scope>
    </dependency>        
```


# Dependencies
Build on top of embed Process Util [de.flapdoodle.embed.process](https://github.com/flapdoodle-oss/de.flapdoodle.embed.process)

# Tested on
 - latest osx;
 - ubuntu precise 32/64;
 - windows 7;

# Known problems
 - some linux distros does not have libaio1.so which is a requirement by latest version of mysql. Proper error is emitted if it's missing;

#License
Use of this source code is governed by a BSD-style license which you can find [here](/LICENSE.md).