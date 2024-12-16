package com.hotel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;


@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @PostConstruct
    public void initializeDatabase() {
        String postgresUrl = "jdbc:postgresql://localhost:5432/postgres";

        try (Connection conn = DriverManager.getConnection(postgresUrl, dbUsername, dbPassword)) {

            String checkDbQuery = "SELECT 1 FROM pg_database WHERE datname = 'hotel_database'";
            ResultSet rs = conn.createStatement().executeQuery(checkDbQuery);

            if (!rs.next()) {

                conn.createStatement().execute("CREATE DATABASE hotel_database");
                System.out.println("Database hotel_database created successfully");

                String hotelDbUrl = "jdbc:postgresql://localhost:5432/hotel_database";
                try (Connection hotelConn = DriverManager.getConnection(hotelDbUrl, dbUsername, dbPassword)) {
                    String createUsersTable = """
                        CREATE TABLE IF NOT EXISTS users (
                            id SERIAL PRIMARY KEY,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password VARCHAR(100) NOT NULL,
                            role VARCHAR(20) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )""";
                    hotelConn.createStatement().execute(createUsersTable);
                    System.out.println("Users table created successfully");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}