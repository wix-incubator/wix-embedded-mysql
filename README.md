# About

Embedded mysql implementation using https://github.com/flapdoodle-oss/de.flapdoodle.embed.process library intended for usage in tests.

# How it works

 - Based on platform where tests are being executed and target mysql version proper vanilla mysql package (tgz, zip) is downloaded from http://dev.mysql.com/get/Downloads/. This is a one-time action, where subsequent invocations use pre-downloaded/cached package.
 - Upon execution needed files are being extracted into **target** folder, database created, service started and post-configuration (user, schema, etc.) performed.
 - On jvm shutdown mysqld process is stopped and temporary files cleaned-up.

#Usage

Add dependency to your pom.xml:

```xml
    <dependency>
        <groupId>com.wixpress.mysql</groupId>
        <artifactId>wix-embedded-mysql</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>        
```

also library depends on **mysql-connector-java** artifact to be provided by project using it.

Basic usage example:

```java
MysqldConfig config = new MysqldConfigBuilder(com.wix.mysql.distribution.Version.v5_5_40).build();

MysqldStarter starter = MysqldStarter.defaultInstance();
MysqldExecutable executable = starter.prepare(config);
 
try {
  executable.start();
  //do work
} finally {
  executable.stop();
}
```

Providing custom schemas, credentials, port:

```java
import static com.wix.mysql.distribution.Version.v5_6_21;
...

final MysqldConfig config = new MysqldConfigBuilder(v5_6_21)
  .withUsername("auser")
  .withPassword("sa")
  .withSchemas(new String[] {"schema1", "schema2"})
  .withPort(9913)
  .build();

MysqldStarter starter = MysqldStarter.defaultInstance();
MysqldExecutable executable = starter.prepare(config);

// you do not have to explicitly stop mysql as it registers to a shutdown hook for that. 
executable.start();

```

As you case see there are no custom user/pass/schema provided for the builder and defaults are taken from:

```java
    com.wix.mysql.config.MysqldConfig.Defaults
```

You can of course override defaults by providing custom values for the builder. Just be aware of:
 - You of course cannot provide null/empty user/schema;
 - You cannot use schema defined in 'com.wix.mysql.config.MysqldConfig.SystemDefaults';
 - You can use system credentials, but you cannot use system user and custom password.

# Accessing Mysql system user/schema

In case you need additional database or users created you can use system credentials defined in:

```java
com.wix.mysql.config.MysqldConfig.SystemDefaults
```

and use them.

# Tested on
 - latest osx;
 - ubuntu precise 32/64;
 - windows 7;

# Known problems
 - some linux distros does not have libaio1.so which is a requirement by latest version of mysql. Proper error is emitted if it's missing;
