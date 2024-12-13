package com.hotel.ui;

import com.hotel.client.HotelApiClient;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

public class RegisterPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<String> roleComboBox;
    private final JLabel managerKeyLabel;
    private final JPasswordField managerKeyField;
    private final JButton registerButton;

    private HotelApiClient apiClient = new HotelApiClient();

    public RegisterPanel() {
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
        roleComboBox = new JComboBox<>(new String[]{"Клиент", "Менеджер"});
        roleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isManager = "Менеджер".equals(roleComboBox.getSelectedItem());
                managerKeyLabel.setVisible(isManager);
                managerKeyField.setVisible(isManager);
                revalidate();
                repaint();
            }
        });
        add(roleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        managerKeyLabel = new JLabel("Личный ключ менеджера:");
        managerKeyLabel.setVisible(false);
        add(managerKeyLabel, gbc);

        gbc.gridx = 1;
        managerKeyField = new JPasswordField(15);
        managerKeyField.setVisible(false);
        add(managerKeyField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        registerButton = new JButton("Зарегистрироваться");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });
        add(registerButton, gbc);

    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Имя пользователя и пароль не должны быть пустыми", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Создание JSON-объекта с данными регистрации
            JSONObject registrationData = new JSONObject();
            registrationData.put("username", username);
            registrationData.put("password", password);
            registrationData.put("role", role);

            // Отправка POST-запроса на сервер
            URL url = new URL("http://localhost:8080/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            // Отправка данных
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = registrationData.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Чтение ответа
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(this, "Регистрация успешна", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при регистрации", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }

            conn.disconnect();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при регистрации: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}