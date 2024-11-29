package main.java.com.hotel.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<String> roleComboBox;
    private final JLabel managerKeyLabel;
    private final JPasswordField managerKeyField;
    private final JButton registerButton;

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
                if ("Manager".equals(roleComboBox.getSelectedItem())) {
                    String managerKey = new String(managerKeyField.getPassword());
                    if (!"secret_key".equals(managerKey)) {
                        JOptionPane.showMessageDialog(RegisterPanel.this, "Invalid Manager Key", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                // Proceed with registration
            }
        });
        add(registerButton, gbc);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getRole() {
        return (String) roleComboBox.getSelectedItem();
    }

    public String getManagerKey() {
        return new String(managerKeyField.getPassword());
    }
}