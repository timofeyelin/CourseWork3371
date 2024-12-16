package com.hotel.ui;

import javax.swing.*;
import com.hotel.ui.Panels.*;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class MainFrame extends JFrame {
    private JTabbedPane authPane;

    public MainFrame() {
        setTitle("Hotel Booking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    public void initUI() {
        authPane = new JTabbedPane();
        LoginPanel loginPanel = new LoginPanel(MainFrame.this);  // Fix this reference
        RegisterPanel registerPanel = new RegisterPanel();

        authPane.addTab("Авторизация", loginPanel);
        authPane.addTab("Регистрация", registerPanel);

        add(authPane);
    }

    public void switchToUserView(String role) {
        getContentPane().removeAll();

        if ("MANAGER".equals(role)) {
            add(new ManagerPanel());
        } else {
            add(new ClientPanel());
        }

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}