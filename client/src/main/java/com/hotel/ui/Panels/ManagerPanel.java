package com.hotel.ui.Panels;

import com.hotel.ui.ManagerOptions.BookingsPanel;
import com.hotel.ui.ManagerOptions.ClientInfoPanel;
import com.hotel.ui.ManagerOptions.RoomsPanel;

import javax.swing.*;
import java.awt.*;

public class ManagerPanel extends JPanel {
    public ManagerPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Управление номерами", new RoomsPanel());
        tabbedPane.addTab("Управление бронированиями", new BookingsPanel());
        tabbedPane.addTab("Информация о клиентах", new ClientInfoPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}