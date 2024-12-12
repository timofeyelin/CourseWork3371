package main.java.com.hotel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConnection {

    private static String url;
    private static String user;
    private static String password;

    static {
        // Загрузка настроек из файла config.properties из classpath
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл 'config.properties' не найден в classpath.");
            }
            // Загружаем свойства
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            if (url == null || user == null || password == null || url.isEmpty() || user.isEmpty()) {
                throw new RuntimeException("Пожалуйста, укажите URL базы данных, имя пользователя и пароль в файле 'config.properties'.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Ошибка при загрузке файла 'config.properties'", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}