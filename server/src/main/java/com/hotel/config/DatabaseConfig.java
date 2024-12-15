package com.hotel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initializeDatabase() {
        try (Connection conn = dataSource.getConnection()) {
            // Проверяем существование БД
            ResultSet resultSet = conn.getMetaData().getCatalogs();
            boolean dbExists = false;
            
            while (resultSet.next()) {
                if ("hotel_database".equals(resultSet.getString(1))) {
                    dbExists = true;
                    break;
                }
            }

            // Создаем БД если не существует
            if (!dbExists) {
                conn.createStatement().execute("CREATE DATABASE hotel_database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}