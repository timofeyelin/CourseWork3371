package com.hotel.ui.Panels;

import com.hotel.client.HotelApiClient;
import com.hotel.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final HotelApiClient apiClient;

    public AdminPanel(MainFrame mainFrame) {
        this.apiClient = new HotelApiClient();

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Имя пользователя менеджера:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Пароль менеджера:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton addButton = new JButton("Добавить менеджера");
        addButton.addActionListener(e -> handleAddManager());
        add(addButton, gbc);

        gbc.gridy = 3;
        JButton logoutButton = new JButton("Выйти");
        logoutButton.addActionListener(e -> mainFrame.switchToLoginRegisterPanel());
        add(logoutButton, gbc);
    }

    private void handleAddManager() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = "MANAGER"; // Устанавливаем роль менеджера

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Имя пользователя и пароль не должны быть пустыми",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String response = apiClient.register(username, password, role);
            JOptionPane.showMessageDialog(this,
                    "Менеджер успешно добавлен",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (Exception e) {
            if (e.getMessage().contains("Username already exists")) {
                JOptionPane.showMessageDialog(this,
                        "Пользователь с таким именем уже существует",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при добавлении менеджера: " + e.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}