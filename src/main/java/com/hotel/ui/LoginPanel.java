package main.java.com.hotel.ui;

import main.java.com.hotel.controller.AuthController;
import main.java.com.hotel.controller.ClientController;
import main.java.com.hotel.controller.ManagerController;
import main.java.com.hotel.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private AuthController authController = new AuthController();

    public LoginPanel(){
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton loginButton = new JButton("Login");
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
            User user = authController.login(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Вход выполнен как " + user.getRole(), "Успех", JOptionPane.INFORMATION_MESSAGE);

                if ("Manager".equals(user.getRole())) {
                    new ManagerController().show();
                } else {
                    new ClientController().show();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Неверное имя пользователя или пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка базы данных", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
