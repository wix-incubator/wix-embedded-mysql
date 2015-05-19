# Wix Embedded MySql [![Build Status](https://travis-ci.org/wix/wix-embedded-mysql.svg?branch=master)](https://travis-ci.org/wix/wix-embedded-mysql)

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

Basic usage example:

```java
import static com.wix.mysql.distribution.Version.v5_5_40;

MysqldConfig config = new MysqldConfigBuilder(v5_5_40).build();

MysqldStarter starter = MysqldStarter.defaultInstance();
MysqldExecutable executable = starter.prepare(config);
 
executable.start();

//do work

// you do not have to explicitly stop mysql as it registers to a shutdown hook for that. 
```

Providing custom schemas, credentials, port:

```java
import static com.wix.mysql.distribution.Version.v5_6_21;

final MysqldConfig config = new MysqldConfigBuilder(v5_6_21)
  .withUsername("auser")
  .withPassword("sa")
  .withSchemas(new String[] {"schema1", "schema2"})
  .withPort(9913)
  .build();

MysqldStarter starter = MysqldStarter.defaultInstance();
MysqldExecutable executable = starter.prepare(config);
executable.start();
```

As you can see there are no custom user/pass/schema provided for the builder and defaults are taken from:

```java
    com.wix.mysql.config.MysqldConfig.Defaults
```

You can of course override defaults by providing custom values for the builder with the following guidelines:
 - You of course cannot provide null/empty user or schema;
 - You cannot use schema defined in 'com.wix.mysql.config.MysqldConfig.SystemDefaults';
 - You can use system credentials, but you cannot use system user and custom password.

# Accessing Mysql system user/schema

In case you need additional database or users created you can use system credentials defined in:

```java
com.wix.mysql.config.MysqldConfig.SystemDefaults
```
and use them.

### Maven

Add dependency to your pom.xml:

```xml
    <dependency>
        <groupId>com.wixpress.mysql</groupId>
        <artifactId>wix-embedded-mysql</artifactId>
        <version>1.0.0</version>
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
