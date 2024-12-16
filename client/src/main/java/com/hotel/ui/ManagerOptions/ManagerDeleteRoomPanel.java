package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class ManagerDeleteRoomPanel extends JPanel {
    public ManagerDeleteRoomPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Удалить номер", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components to delete room
        JPanel deleteRoomForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        deleteRoomForm.add(new JLabel("Номер:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        deleteRoomForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        deleteRoomForm.add(new JButton("Удалить номер"), gbc);

        add(deleteRoomForm, BorderLayout.CENTER);
    }
}