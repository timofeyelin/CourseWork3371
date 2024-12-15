package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class ManagerEditBookingPanel extends JPanel {
    public ManagerEditBookingPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Редактировать бронирование", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components to edit booking
        JPanel editBookingForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        editBookingForm.add(new JLabel("ID бронирования:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        editBookingForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        editBookingForm.add(new JLabel("Новая дата начала бронирования:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        editBookingForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        editBookingForm.add(new JLabel("Новая дата конца бронирования:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        editBookingForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        editBookingForm.add(new JButton("Редактировать бронирование"), gbc);

        add(editBookingForm, BorderLayout.CENTER);
    }
}