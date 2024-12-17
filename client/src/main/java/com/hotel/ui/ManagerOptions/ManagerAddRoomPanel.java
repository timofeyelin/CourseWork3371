package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import com.hotel.client.HotelApiClient;

public class ManagerAddRoomPanel extends JPanel {
    public ManagerAddRoomPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Добавить новый номер", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JPanel addRoomForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        addRoomForm.add(new JLabel("Номер:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField numberField = new JTextField(15);
        addRoomForm.add(numberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addRoomForm.add(new JLabel("Тип номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField typeField = new JTextField(15);
        addRoomForm.add(typeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addRoomForm.add(new JLabel("Цена:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField priceField = new JTextField(15);
        addRoomForm.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addRoomForm.add(new JLabel("Описание:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextArea descriptionArea = new JTextArea(5, 15);
        addRoomForm.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Добавить номер");
        addRoomForm.add(addButton, gbc);

        add(addRoomForm, BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            String number = numberField.getText();
            String type = typeField.getText();
            String priceText = priceField.getText();
            String description = descriptionArea.getText();
            try {
                BigDecimal price = new BigDecimal(priceText);
                HotelApiClient apiClient = new HotelApiClient();
                apiClient.addRoom(number, type, price, description);
                JOptionPane.showMessageDialog(this, "Номер успешно добавлен");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении номера: " + ex.getMessage());
            }
        });
    }
}