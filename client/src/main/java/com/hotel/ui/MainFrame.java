package com.hotel.ui;

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
        tabbedPane.addTab("Авторизация", new LoginPanel());
        tabbedPane.addTab("Регистрация", new RegisterPanel());

        // Добавить панель с вкладками в окно
        add(tabbedPane);
    }

    public static void main(String[] args) {

        // Запуск интерфейса
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}