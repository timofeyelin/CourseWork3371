package com.hotel.ui;

import com.hotel.client.HotelApiClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RegisterPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<String> roleComboBox;
    private final HotelApiClient apiClient;

    public RegisterPanel() {
        this.apiClient = new HotelApiClient();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

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

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Роль:"), gbc);

        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"CLIENT", "MANAGER"});
        add(roleComboBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JButton registerButton = new JButton("Зарегистрироваться");
        registerButton.addActionListener(e -> handleRegistration());
        add(registerButton, gbc);
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

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
        roleComboBox.setSelectedIndex(0);
    }
}