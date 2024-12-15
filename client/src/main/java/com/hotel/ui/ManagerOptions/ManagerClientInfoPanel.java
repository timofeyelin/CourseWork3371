package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class ManagerClientInfoPanel extends JPanel {
    public ManagerClientInfoPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Информация о клиентах", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Add components to manage client information
        JTextArea clientInfoTextArea = new JTextArea();
        clientInfoTextArea.setEditable(false);
        add(new JScrollPane(clientInfoTextArea), BorderLayout.CENTER);

        // Example data
        clientInfoTextArea.setText("Клиент 1: John Doe, Почта: john@example.com\nКлиент 2: Jane Smith, Почта: jane@example.com");
    }
}
