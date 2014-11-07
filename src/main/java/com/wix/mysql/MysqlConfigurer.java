package com.wix.mysql;

import com.wix.mysql.config.MysqldConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * @author viliusl
 * @since 06/11/14
 */
public class MysqlConfigurer {

    private Logger log = Logger.getLogger(getClass().getName());

    private final MysqldConfig config;

    public MysqlConfigurer(MysqldConfig config) { this.config = config; }



    public void configure() {
        if (!config.isDefaultUsername() || !config.isDefaultSchema()) {
            Connection con = null;
            try {
                con = DriverManager.getConnection(
                        connectionStringFor(config),
                        MysqldConfig.Defaults.USERNAME,
                        MysqldConfig.Defaults.PASSWORD);

                if (!config.isDefaultSchema()) {
                    executeStmt(con, String.format("create database %s;", config.getSchema()));
                }

                if (!config.isDefaultUsername()) {
                    executeStmt(con, String.format("CREATE USER '%s'@'localhost' IDENTIFIED BY '%s';", config.getUsername(), config.getPassword()));
                    executeStmt(con, String.format("GRANT ALL ON %s.* TO '%s'@'localhost';", config.getSchema(), config.getUsername()));
                }

            } catch (Exception e) {
                log.severe(e.getMessage());
                throw new RuntimeException(e);
            } finally {
                try { if ( con != null ) con.close(); } catch (SQLException e) { throw new RuntimeException(e); }
            }
        }
    }

    private void executeStmt(Connection con, String sql) {
        Statement stmt = null;
        try {
            log.info("Executing: " + sql);
            stmt = con.createStatement();
            stmt.execute(sql);
            log.info("Executing: " + sql + " Success.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String connectionStringFor(MysqldConfig config) {
        return String.format("jdbc:mysql://localhost:%s/%s", config.getPort(), MysqldConfig.Defaults.SCHEMA);
    }
}
