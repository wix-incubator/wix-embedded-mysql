package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.SchemaConfig;
import org.junit.Ignore;
import org.junit.Test;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.ScriptResolver.classPathFile;
import static com.wix.mysql.ScriptResolver.classPathFiles;
import static com.wix.mysql.config.Charset.LATIN1;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.config.SchemaConfig.aSchemaConfig;
import static com.wix.mysql.distribution.Version.v5_6_23;
import static com.wix.mysql.distribution.Version.v5_6_latest;

/**
 * @author viliusl
 * @since 21/07/15
 */
@Ignore
public class JavaUsageExamplesTest {

    @Test
    public void defaultConfigurationAndASingleSchema() {
        EmbeddedMysql mysqld = anEmbeddedMysql(v5_6_latest)
                .addSchema("aschema", classPathFile("db/001_init.sql"))
                .start();

        //do work

        mysqld.stop(); //optional, as there is a shutdown hook
    }

    @Test
    public void defaultConfigurationAndASingleSchemaWithMultipleMigrations() {
        EmbeddedMysql mysqld = anEmbeddedMysql(v5_6_latest)
                .addSchema("aschema", classPathFiles("db/*.sql"))
                .start();

        //do work

        mysqld.stop(); //optional, as there is a shutdown hook
    }

    @Test
    public void mysqldConfigAndMultipleSchemas() {
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
    }

    @Test
    public void schemaConfigViaBuilder() {
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
    }

    @Test
    public void schemaReset() {
        EmbeddedMysql mysqld = anEmbeddedMysql(v5_6_latest)
                .addSchema("aschema", classPathFile("db/001_init.sql"))
                .start();

        //do work

        mysqld.reloadSchema("aschema", classPathFile("db/001_init.sql"));

        //continue on doing work

        mysqld.stop(); //optional, as there is a shutdown hook
    }

}
