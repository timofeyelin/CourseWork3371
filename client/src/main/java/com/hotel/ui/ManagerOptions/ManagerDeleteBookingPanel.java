package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class ManagerDeleteBookingPanel extends JPanel {
    public ManagerDeleteBookingPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Удалить бронирование", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components to delete booking
        JPanel deleteBookingForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        deleteBookingForm.add(new JLabel("ID бронирования:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        deleteBookingForm.add(new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        deleteBookingForm.add(new JButton("Удалить бронирование"), gbc);

        add(deleteBookingForm, BorderLayout.CENTER);
    }
}