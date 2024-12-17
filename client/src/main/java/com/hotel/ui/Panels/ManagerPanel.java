package com.hotel.ui.Panels;

import com.hotel.ui.ManagerOptions.BookingsPanel;
import com.hotel.ui.ManagerOptions.ClientInfoPanel;
import com.hotel.ui.ManagerOptions.RoomsPanel;
import com.hotel.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class ManagerPanel extends JPanel {
    public ManagerPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Управление номерами", new RoomsPanel());
        tabbedPane.addTab("Управление бронированиями", new BookingsPanel());
        tabbedPane.addTab("Информация о клиентах", new ClientInfoPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Выйти");
        logoutButton.addActionListener(e -> mainFrame.switchToLoginRegisterPanel());
        add(logoutButton, BorderLayout.SOUTH);
    }
}