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

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private HotelApiClient apiClient = new HotelApiClient();

    public LoginPanel(){
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

        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton loginButton = new JButton("Войти");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        add(loginButton, gbc);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Имя пользователя и пароль не должны быть пустыми", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Создание JSON-объекта с данными пользователя
            JSONObject loginData = new JSONObject();
            loginData.put("username", username);
            loginData.put("password", password);

            // Отправка POST-запроса на сервер
            URL url = new URL("http://localhost:8080/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            // Отправка данных
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = loginData.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Чтение ответа
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(this, "Вход выполнен успешно", "Успех", JOptionPane.INFORMATION_MESSAGE);
                // Дополнительные действия после успешного входа
            } else {
                JOptionPane.showMessageDialog(this, "Неверное имя пользователя или пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }

            conn.disconnect();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при авторизации: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
