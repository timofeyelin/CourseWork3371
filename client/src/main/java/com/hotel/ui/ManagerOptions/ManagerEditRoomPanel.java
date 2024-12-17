package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import com.hotel.client.HotelApiClient;

public class ManagerEditRoomPanel extends JPanel {
    public ManagerEditRoomPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Редактировать информацию о номере", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Добавляем компоненты для редактирования информации о номере
        JPanel editRoomForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        editRoomForm.add(new JLabel("ID Номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField idField = new JTextField(15);
        editRoomForm.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        editRoomForm.add(new JLabel("Новый тип номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField typeField = new JTextField(15);
        editRoomForm.add(typeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        editRoomForm.add(new JLabel("Новая цена:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField priceField = new JTextField(15);
        editRoomForm.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        editRoomForm.add(new JLabel("Новое описание:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextArea descriptionArea = new JTextArea(5, 15);
        editRoomForm.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JCheckBox keepDescriptionCheckBox = new JCheckBox("Оставить прежнее описание");
        editRoomForm.add(keepDescriptionCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton editButton = new JButton("Редактировать номер");
        editRoomForm.add(editButton, gbc);

        add(editRoomForm, BorderLayout.CENTER);

        editButton.addActionListener(e -> {
            String idText = idField.getText();
            String newType = typeField.getText();
            String priceText = priceField.getText();
            String description = descriptionArea.getText();
            boolean keepDescription = keepDescriptionCheckBox.isSelected();

            try {
                Long roomId = Long.parseLong(idText);
                BigDecimal newPrice = new BigDecimal(priceText);
                HotelApiClient apiClient = new HotelApiClient();
                apiClient.editRoom(roomId, newType, newPrice, keepDescription ? null : description);
                JOptionPane.showMessageDialog(this, "Номер успешно обновлен");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении номера: " + ex.getMessage());
            }
        });
    }
}