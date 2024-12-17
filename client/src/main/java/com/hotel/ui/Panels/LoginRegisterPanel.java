package com.hotel.ui.Panels;

import com.hotel.client.HotelApiClient;
import com.hotel.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LoginRegisterPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final HotelApiClient apiClient;
    private final MainFrame mainFrame;

    public LoginRegisterPanel(MainFrame frame) {
        this.mainFrame = frame;
        this.apiClient = new HotelApiClient();

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Имя пользователя:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton loginButton = new JButton("Войти");
        loginButton.addActionListener(e -> handleLogin());
        add(loginButton, gbc);

        gbc.gridy = 3;
        JButton registerButton = new JButton("Зарегистрироваться");
        registerButton.addActionListener(e -> handleRegistration());
        add(registerButton, gbc);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            String response = apiClient.login(username, password);
            JOptionPane.showMessageDialog(this,
                    "Вход выполнен успешно",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);

            // Получаем роль из ответа и переключаем view
            String role;
            if (response.contains("ADMIN")) {
                role = "ADMIN";
            } else if (response.contains("MANAGER")) {
                role = "MANAGER";
            } else {
                role = "CLIENT";
            }
            mainFrame.switchToUserView(role);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = "CLIENT"; // Устанавливаем роль по умолчанию

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Имя пользователя и пароль не должны быть пустыми",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            apiClient.register(username, password, role);
            JOptionPane.showMessageDialog(this,
                    "Регистрация успешна",
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
                        "Ошибка при регистрации: " + e.getMessage(),
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