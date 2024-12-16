package com.hotel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initializeDatabase() {
        try (Connection conn = dataSource.getConnection()) {

            String checkDbQuery = "SELECT 1 FROM pg_database WHERE datname = 'hotel_database'";
            boolean dbExists = conn.createStatement()
                    .executeQuery(checkDbQuery)
                    .next();

            if (!dbExists) {
                conn.createStatement().execute("CREATE DATABASE hotel_database");
                System.out.println("Database hotel_database created successfully");
            } else {
                System.out.println("Database hotel_database already exists");
            }
        } catch (SQLException e) {

            System.err.println("Database initialization error: " + e.getMessage());
        }
    }
}