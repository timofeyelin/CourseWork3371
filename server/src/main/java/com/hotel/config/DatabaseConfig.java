package com.hotel.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
        String url = dbUrl.substring(0, dbUrl.lastIndexOf("/"));
        String dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);

        try (Connection conn = DriverManager.getConnection(url + "/postgres", dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {

            // Проверка существования базы данных
            var rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "';");
            if (!rs.next()) {
                // Создание базы данных, если не существует
                stmt.executeUpdate("CREATE DATABASE " + dbName + ";");
                System.out.println("База данных " + dbName + " успешно создана.");
            } else {
                System.out.println("База данных " + dbName + " уже существует.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Не удалось инициализировать базу данных: " + e.getMessage(), e);
        }
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(dbUrl)
                .username(dbUsername)
                .password(dbPassword)
                .build();
    }
}