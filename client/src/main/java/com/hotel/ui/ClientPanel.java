package com.hotel.ui;

import javax.swing.*;
import java.awt.*;

public class ClientPanel extends JPanel {
    public ClientPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Панель клиента", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}