package main.java.com.hotel.ui;

import main.java.com.hotel.util.DatabaseInitializer;

import javax.swing.*;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Hotel Booking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Добавление компонентов в окно
        initUI();
    }

    public void initUI() {
        // Создание панели с вкладками
        JTabbedPane tabbedPane = new JTabbedPane();

        // Добавление вкладок
        tabbedPane.addTab("Login", new LoginPanel());
        tabbedPane.addTab("Register", new RegisterPanel());

        // Добавить панель с вкладками в окно
        add(tabbedPane);
    }

    public static void main(String[] args) {
        // Загрузка настроек из файла config.properties
        Properties props = new Properties();
        try (InputStream input = MainFrame.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Файл 'config.properties' не найден в classpath.");
                return;
            }
            props.load(input);
        } catch (IOException e) {
            System.out.println("Ошибка загрузки файла 'config.properties'.");
            e.printStackTrace();
            return;
        }

        String dbUser = props.getProperty("db.user");
        String dbPassword = props.getProperty("db.password");

        if (dbUser == null || dbPassword == null || dbUser.isEmpty()) {
            System.out.println("Пожалуйста, укажите имя пользователя и пароль в файле 'config.properties'.");
            return;
        }

        // Инициализация базы данных
        DatabaseInitializer.initializeDatabase(dbUser, dbPassword);

        // Запуск интерфейса
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}