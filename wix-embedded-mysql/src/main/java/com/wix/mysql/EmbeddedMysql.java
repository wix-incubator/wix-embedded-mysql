package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;

/**
 * @author viliusl
 * @since 07/06/15
 */
public class EmbeddedMysql {
    private final MysqldConfig config;

    private EmbeddedMysql(final MysqldConfig config) {
        this.config = config;
    }
}

//trait EmbeddedMysql {
//        def addSchema(schemaConfig: SchemaConfig): EmbeddedMysql = ???
//        def apply(schemaConfig: SchemaConfig, files: Seq[File]): EmbeddedMysql = ???
//        def apply(schemaConfig: SchemaConfig, file: File): EmbeddedMysql = ???
//        def dataSourceFor(schemaConfig: SchemaConfig): DataSource = ???
//        def getMysqldConfig(): MysqldConfig = ???
//        def getUsername(): String = ???
//        def getPassword(): String = ???
//        def getJdcConnectionUrl(): String = ???
//        def stop(): Unit = ???
//        }
//
//        object EmbeddedMysql {
//        def Builder(version: Version): Builder = new Builder(MysqldConfig.Builder(version).build)
//        def Builder(version: Version, port: Int): Builder = new Builder(MysqldConfig.Builder(version).withPort(port).build)
//        def Builder(config: MysqldConfig): Builder = new Builder(config)
//
//class Builder(config: MysqldConfig) {
//        def withUser(username: String, password: String): Builder = ???
//        def addSchema(schemaConfig: SchemaConfig): Builder = ???
//        def start(): EmbeddedMysql = ???
//        }
//        }

