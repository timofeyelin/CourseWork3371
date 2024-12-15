package com.hotel.ui.ClientOptions;

import javax.swing.*;
import java.awt.*;

public class ClientRoomSearchPanel extends JPanel {
    public ClientRoomSearchPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Найти номер", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components for searching rooms by type and date
        JPanel searchForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        searchForm.add(new JLabel("Тип номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        searchForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        searchForm.add(new JLabel("Дата начала бронирования:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        searchForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        searchForm.add(new JLabel("Дата конца бронирования:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        searchForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        searchForm.add(new JButton("Найти номер"), gbc);

        add(searchForm, BorderLayout.CENTER);
    }
}