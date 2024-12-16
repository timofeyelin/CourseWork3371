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
            // Проверка существования БД
            String checkDbQuery = "SELECT 1 FROM pg_database WHERE datname = 'hotel_database'";
            boolean dbExists = conn.createStatement()
                    .executeQuery(checkDbQuery)
                    .next();

            if (!dbExists) {

                conn.createStatement().execute("CREATE DATABASE hotel_database");
                System.out.println("Database hotel_database created successfully");

                Connection hotelDbConn = dataSource.getConnection();


                String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(50) UNIQUE NOT NULL,
                        password VARCHAR(100) NOT NULL,
                        role VARCHAR(20) NOT NULL
                    )
                """;
                hotelDbConn.createStatement().execute(createUsersTable);
                System.out.println("Table 'users' created successfully");

                hotelDbConn.close();
            } else {
                System.out.println("Database hotel_database already exists");
            }
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}