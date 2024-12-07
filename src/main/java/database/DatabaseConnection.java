package main.java.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConnection {
    public static void main(String[] args) {
        // Подключаемся к системной базе данных 'postgres'
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "123";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Подключено к базе данных 'postgres'");

            // Проверяем, существует ли база данных 'hotel_database'
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = 'hotel_database'");
            if (!rs.next()) {
                // База данных не существует, создаем ее
                stmt.executeUpdate("CREATE DATABASE hotel_database");
                System.out.println("База данных 'hotel_database' создана");
            } else {
                System.out.println("База данных 'hotel_database' уже существует");
            }

            // Закрываем соединение с базой данных 'postgres'
            conn.close();

            // Подключаемся к базе данных 'hotel_database'
            String urlMyDb = "jdbc:postgresql://localhost:5432/hotel_database";
            Connection hotelDbConn = DriverManager.getConnection(urlMyDb, user, password);
            System.out.println("Подключено к базе данных 'hotel_database'");

            // Создаем таблицу 'example_table', если она не существует
            Statement myDbStmt = hotelDbConn.createStatement();

            // Создаем таблицу 'users', если она не существует
            String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id SERIAL PRIMARY KEY, "
                    + "username VARCHAR(50) UNIQUE NOT NULL, "
                    + "password VARCHAR(256) NOT NULL, "
                    + "role VARCHAR(20) NOT NULL"
                    + ")";
            myDbStmt.executeUpdate(createUsersTableSql);
            System.out.println("Таблица 'users' проверена или создана успешно");

            // Закрываем соединение с базой данных 'hotel_database'
            hotelDbConn.close();

        } catch (SQLException e) {
            System.out.println("Ошибка подключения к базе данных");
            e.printStackTrace();
        }
    }
}