package com.hotel.ui.ClientOptions;

import javax.swing.*;
import java.awt.*;

public class ClientAvailableRoomsPanel extends JPanel {
    public ClientAvailableRoomsPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Доступные номера", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components to display available rooms
        JTextArea roomsTextArea = new JTextArea();
        roomsTextArea.setEditable(false);
        add(new JScrollPane(roomsTextArea), BorderLayout.CENTER);

        // Example data
        roomsTextArea.setText("Номер 101: Одиночный\nНомер102: Для двоих\nНомер 103: Люкс");
    }
}
