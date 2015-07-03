package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.SchemaConfig;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

import static java.lang.String.format;

/**
 * @author viliusl
 * @since 30/06/15
 */
public class Datasource {

    //TODO: reuse, create a provider
    public static DataSource aDataSource(final MysqldConfig config, final SchemaConfig schema) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(getJdbcConnectionUrl(config, schema.getName()));
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        return dataSource;
    }

    //TODO: reuse
    public static DataSource aDataSource(final MysqldConfig config, final String schemaName) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(getJdbcConnectionUrl(config, schemaName));
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        return dataSource;
    }


    //TODO: support custom host
    public static String getJdbcConnectionUrl(final MysqldConfig config, final String schemaName) {
        return format("jdbc:mysql://localhost:%s/%s", config.getPort(), schemaName);
    }

}
