package com.hotel.ui;

import javax.swing.*;
import java.awt.*;

public class ManagerPanel extends JPanel {
    public ManagerPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Панель менеджера", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}