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
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final HotelApiClient apiClient;
    private MainFrame mainFrame;


    public LoginPanel(MainFrame frame){

        this.mainFrame = frame;
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

        try {
            String response = apiClient.login(username, password);
            JOptionPane.showMessageDialog(this,
                    "Вход выполнен успешно",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);

            // Получаем роль из ответа и переключаем view
            String role = response.contains("MANAGER") ? "MANAGER" : "CLIENT";
            mainFrame.switchToUserView(role);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
