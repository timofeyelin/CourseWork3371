package main.java.com.hotel.util;

import java.sql.*;

public class DatabaseInitializer {

    public static void initializeDatabase(String user, String password) {
        String url = "jdbc:postgresql://localhost:5432/hotel_database";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Подключено к базе данных 'hotel_database'");

        } catch (SQLException e) {
            if ("3D000".equals(e.getSQLState())) {
                System.out.println("База данных 'hotel_database' не найдена. Пытаемся создать...");

                try {

                    String templateUrl = "jdbc:postgresql://localhost:5432/template1";
                    Connection adminConn = DriverManager.getConnection(templateUrl, user, password);

                    Statement stmt = adminConn.createStatement();
                    String createDatabaseSql = "CREATE DATABASE hotel_database";
                    stmt.executeUpdate(createDatabaseSql);
                    System.out.println("База данных 'hotel_database' успешно создана.");

                    adminConn.close();

                    conn = DriverManager.getConnection(url, user, password);
                    System.out.println("Подключено к базе данных 'hotel_database'");

                } catch (SQLException ex) {
                    System.out.println("Не удалось создать базу данных 'hotel_database'.");
                    ex.printStackTrace();
                    return;
                }
            } else {
                System.out.println("Ошибка подключения к базе данных:");
                e.printStackTrace();
                return;
            }
        }

        // Создание необходимых таблиц
        try {
            Statement stmt = conn.createStatement();

            // Создаем таблицу 'users', если она не существует
            String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id SERIAL PRIMARY KEY, "
                    + "username VARCHAR(50) UNIQUE NOT NULL, "
                    + "password VARCHAR(256) NOT NULL, "
                    + "role VARCHAR(20) NOT NULL"
                    + ")";
            stmt.executeUpdate(createUsersTableSql);
            System.out.println("Таблица 'users' проверена или создана успешно");

            conn.close();

        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблиц:");
            e.printStackTrace();
        }
    }
}