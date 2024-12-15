package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class ManagerViewBookingsPanel extends JPanel {
    public ManagerViewBookingsPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Посмотреть все бронирования", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components to view all bookings
        JTextArea bookingsTextArea = new JTextArea();
        bookingsTextArea.setEditable(false);
        add(new JScrollPane(bookingsTextArea), BorderLayout.CENTER);

        // Example data
        bookingsTextArea.setText("Бронирование 1: Номер 101, Дата начала бронирования: 2023-01-01, Дата конца бронирования: 2023-01-05\n" +
                "Бронирование 2: Номер 102, Дата начала бронирования: 2023-02-01, Дата конца бронирования: 2023-02-05");
    }
}