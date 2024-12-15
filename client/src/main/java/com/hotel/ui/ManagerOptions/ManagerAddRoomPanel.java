package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class ManagerAddRoomPanel extends JPanel {
    public ManagerAddRoomPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Добавить новый номер", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components to add new room
        JPanel addRoomForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        addRoomForm.add(new JLabel("Номер:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        addRoomForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addRoomForm.add(new JLabel("Тип номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        addRoomForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addRoomForm.add(new JLabel("Цена:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        addRoomForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addRoomForm.add(new JButton("Добавить номер"), gbc);

        add(addRoomForm, BorderLayout.CENTER);
    }
}
