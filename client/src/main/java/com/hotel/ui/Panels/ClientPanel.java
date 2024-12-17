package com.hotel.ui.Panels;

import com.hotel.ui.ClientOptions.*;

import javax.swing.*;
import java.awt.*;
import com.hotel.ui.MainFrame;

public class ClientPanel extends JPanel {
    public ClientPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());
                
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Доступные номера", new ClientAvailableRoomsPanel());
        tabbedPane.addTab("Новое бронирование", new ClientNewBookingPanel());
        tabbedPane.addTab("Мои бронирования", new ClientMyBookingsPanel());
        tabbedPane.addTab("Найти номер", new ClientRoomSearchPanel());
        tabbedPane.addTab("Тест", new ClientRoomBookingPanel());
        
        add(tabbedPane, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Выйти");
        logoutButton.addActionListener(e -> mainFrame.switchToLoginRegisterPanel());
        add(logoutButton, BorderLayout.SOUTH);
    }
}