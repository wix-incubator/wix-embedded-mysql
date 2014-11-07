# About

Embedded mysql implementation using https://github.com/flapdoodle-oss/de.flapdoodle.embed.process library intended for usage in tests.

# How it works

 - Based on platform where tests are being executed and target mysql version proper vanilla mysql package (tgz, zip) is downloaded from http://dev.mysql.com/get/Downloads/. This is a one-time action, where subsequent invocations use pre-downloaded/cached package.
 - Upon exeution needed files are being extracted into **target** folder, database created, service started and post-configuration (user, schema, etc.) performed.
 - On jvm shutdown mysqld process is stopped and temporary files cleaned-up.

#Usage

Add dependency to your pom.xml:

```xml
    <dependency>
        <groupId>com.wixpress.mysql</groupId>
        <artifactId>wix-embedded-mysql</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <scope>test</scope>
    <dependency>        
```

also library depends on mysql-connector-java artifact to be provided by project using it.

Basic usage example:

```java
        MysqldConfig config = new MysqldConfigBuilder(com.wix.mysql.distribution.Version.v5_5_40)
                .withUsername("auser")
                .withPassword("sa")
                .withSchema("some_schema")
                .withPort(9913)
                .build();

        MysqldStarter starter = MysqldStarter.defaultInstance();
        MysqldExecutable executable = starter.prepare(config);
        
        try {
            executable.start();
            //do work
        } finally {
            executable.stop();
        }
```

# Works on
 - latest osx;
 - ubuntu precise 32/64;

# Known problems
 - some linux distros does not have libaio1.so which is a requirement by latest version of mysql. Proper error is emitted if it's missing;
 - windows version does not work - pull req pending towards https://github.com/flapdoodle-oss/de.flapdoodle.embed.process;