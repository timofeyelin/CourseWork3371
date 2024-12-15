package com.hotel.ui.ClientOptions;

import javax.swing.*;
import java.awt.*;

public class ClientNewBookingPanel extends JPanel {
    public ClientNewBookingPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Забронировать номер", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components to create new booking
        JPanel bookingForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        bookingForm.add(new JLabel("Номер:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        bookingForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        bookingForm.add(new JLabel("Дата начала бронирования:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        bookingForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        bookingForm.add(new JLabel("Дата конца бронирования:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        bookingForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        bookingForm.add(new JButton("Забронировать"), gbc);

        add(bookingForm, BorderLayout.CENTER);
    }
}