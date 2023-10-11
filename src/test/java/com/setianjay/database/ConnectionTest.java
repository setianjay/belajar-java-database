package com.setianjay.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class ConnectionTest {

    @Test
    void testCreateConnection() {
        // format jdbc is "jdbc:{dbms_name}://{host}:{port}/{database_name}
        String jdbcUrl = "jdbc:mysql://localhost:3306/belajar_java_database";
        String username = "root";
        String password = "";

        try (Connection ignored = DriverManager.getConnection(jdbcUrl, username, password)){
            System.out.println("Success to connect MySQL");
        } catch (SQLException exception) {
            exception.printStackTrace();
            Assertions.fail(exception);
        }
    }


    @Test
    @DisplayName(value = "Create connection pool with HikariCP in the first way using hardcoding configuration")
    void testCreateConnectionPoolWithHikariCPInTheFirstWay() {
        try {
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


            HikariDataSource hDataSource = new HikariDataSource(hConfig);
            Connection connection = hDataSource.getConnection();

            connection.close(); // if connection is closed, the connection will be returned to the pool
            hDataSource.close(); // if hikari data source is closed, the pool we be closed
        }catch (SQLException exception){
            exception.printStackTrace();
            Assertions.fail(exception);
        }
    }

    @Test
    @DisplayName(value = "Create connection pool HikariCP in the second way using properties file")
    void testCreateConnectionPoolWithHikariCPInTheSecondWay() {
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream); // read configuration in properties file

            HikariConfig hConfig = new HikariConfig(properties);
            HikariDataSource hDataSource = new HikariDataSource(hConfig);
            Connection connection = hDataSource.getConnection();

            connection.close(); // if connection is closed, the connection will be returned to the pool
            hDataSource.close(); // if hikari data source is closed, the pool we be closed
        }catch (SQLException | IOException exception){
            exception.printStackTrace();
            Assertions.fail(exception);
        }
    }

}
