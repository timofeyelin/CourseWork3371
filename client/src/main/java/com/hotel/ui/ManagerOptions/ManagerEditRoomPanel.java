package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class ManagerEditRoomPanel extends JPanel {
    public ManagerEditRoomPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Редактировать информацию о номере", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components to edit room information
        JPanel editRoomForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        editRoomForm.add(new JLabel("Номер:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        editRoomForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        editRoomForm.add(new JLabel("Новый тип номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        editRoomForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        editRoomForm.add(new JLabel("Новая цена:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        editRoomForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        editRoomForm.add(new JButton("Редактировать номер"), gbc);

        add(editRoomForm, BorderLayout.CENTER);
    }
}