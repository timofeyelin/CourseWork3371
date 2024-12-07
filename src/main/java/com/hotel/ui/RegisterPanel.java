package main.java.com.hotel.ui;

import main.java.com.hotel.controller.AuthController;
import main.java.com.hotel.service.ManagerKeyService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<String> roleComboBox;
    private final JLabel managerKeyLabel;
    private final JPasswordField managerKeyField;
    private final JButton registerButton;

    private AuthController authController = new AuthController();

    public RegisterPanel() {
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

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"Client", "Manager"});
        roleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isManager = "Manager".equals(roleComboBox.getSelectedItem());
                managerKeyLabel.setVisible(isManager);
                managerKeyField.setVisible(isManager);
                revalidate();
                repaint();
            }
        });
        add(roleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        managerKeyLabel = new JLabel("Manager Key:");
        managerKeyLabel.setVisible(false);
        add(managerKeyLabel, gbc);

        gbc.gridx = 1;
        managerKeyField = new JPasswordField(15);
        managerKeyField.setVisible(false);
        add(managerKeyField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        registerButton = new JButton("Register");
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

        if ("Manager".equals(role)) {
            String managerKey = managerKeyField.getText();
            if (!ManagerKeyService.validateManagerKey(managerKey)) {
                JOptionPane.showMessageDialog(this, "Неверный ключ менеджера", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            authController.register(username, password, role);
            JOptionPane.showMessageDialog(this, "Регистрация успешна", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка базы данных", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}