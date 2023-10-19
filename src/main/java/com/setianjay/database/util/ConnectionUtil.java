package com.setianjay.database.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionUtil {

    private ConnectionUtil() {
    }

    private static HikariDataSource hikariDataSource;


    public static HikariDataSource getHikariDataSource() {
        if (hikariDataSource == null) {
            setHikariDataSource();
        }

        return hikariDataSource;
    }

    private static void setHikariDataSource() {
        HikariConfig hConfig = new HikariConfig();
        /* setup source of database */
        hConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hConfig.setJdbcUrl("jdbc:mysql://localhost:3306/belajar_java_database");
        hConfig.setUsername("root");
        hConfig.setPassword("");

        /* setup connection pool */
        hConfig.setMaximumPoolSize(10); // size of connection pool (consist 10 database connection)
        // if no one use the connection until idle timeout, the half of connection will be close
        hConfig.setMinimumIdle(5);
        hConfig.setIdleTimeout(60_000L);
        hConfig.setConnectionTimeout(10 * 60_000L); // waiting time for the connection

        hikariDataSource = new HikariDataSource(hConfig);
    }

    /**
     * Close Hikari Connection Pool
     * */
    public static void close(){
        if (hikariDataSource != null){
            hikariDataSource.close();
            hikariDataSource = null;
        }
    }
}
