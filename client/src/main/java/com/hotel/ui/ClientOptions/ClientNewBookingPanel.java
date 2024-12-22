package com.hotel.ui.ClientOptions;

import javax.swing.*;
import java.awt.*;
import com.hotel.client.HotelApiClient;

public class ClientNewBookingPanel extends JPanel {
    public ClientNewBookingPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Забронировать номер", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JPanel bookingForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        bookingForm.add(new JLabel("Номер комнаты:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField roomNumberField = new JTextField(15);
        bookingForm.add(roomNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        bookingForm.add(new JLabel("Дата начала (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField startDateField = new JTextField(15);
        bookingForm.add(startDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        bookingForm.add(new JLabel("Дата конца (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField endDateField = new JTextField(15);
        bookingForm.add(endDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton bookButton = new JButton("Забронировать");
        bookingForm.add(bookButton, gbc);

        add(bookingForm, BorderLayout.CENTER);

        bookButton.addActionListener(e -> {
            String roomNumber = roomNumberField.getText();
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();
    
            try {
                Long userId = HotelApiClient.getCurrentUserId();
                HotelApiClient apiClient = new HotelApiClient();
                apiClient.createBooking(roomNumber, userId, startDate, endDate);
                JOptionPane.showMessageDialog(this, "Бронирование успешно создано");
                roomNumberField.setText("");
                startDateField.setText("");
                endDateField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при создании бронирования: " + ex.getMessage());
            }
        });
    }
}