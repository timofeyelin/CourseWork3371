package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;
import com.hotel.client.HotelApiClient;

public class ManagerDeleteRoomPanel extends JPanel {
    public ManagerDeleteRoomPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Удалить номер", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JPanel deleteRoomForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        deleteRoomForm.add(new JLabel("ID Номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField idField = new JTextField(15);
        deleteRoomForm.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton deleteButton = new JButton("Удалить номер");
        deleteRoomForm.add(deleteButton, gbc);

        add(deleteRoomForm, BorderLayout.CENTER);

        deleteButton.addActionListener(e -> {
            String idText = idField.getText();

            try {
                Long roomId = Long.parseLong(idText);
                HotelApiClient apiClient = new HotelApiClient();
                apiClient.deleteRoom(roomId);
                JOptionPane.showMessageDialog(this, "Номер успешно удален");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении номера: " + ex.getMessage());
            }
        });
    }
}