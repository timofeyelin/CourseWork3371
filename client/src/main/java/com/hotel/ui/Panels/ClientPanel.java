package com.hotel.ui.Panels;

import com.hotel.ui.ClientOptions.*;

import javax.swing.*;

import java.awt.*;
import com.hotel.ui.MainFrame;

public class ClientPanel extends JPanel {
    public ClientPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());
                
        JTabbedPane tabbedPane = new JTabbedPane();
        ClientRoomBookingPanel roomBookingPanel = new ClientRoomBookingPanel();
        ClientMyBookingsPanel myBookingsPanel = new ClientMyBookingsPanel();
        
        tabbedPane.addTab("Мои бронирования", myBookingsPanel);
        tabbedPane.addTab("Забронировать номер", roomBookingPanel);
        tabbedPane.addTab("Выйти", new JPanel());
        
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 1) { // "Забронировать номер" tab
                roomBookingPanel.refreshRoomGrid();
            } else if (selectedIndex == 0) { // "Мои бронирования" tab
                myBookingsPanel.refreshRoomGrid();
            } else if (selectedIndex == tabbedPane.getTabCount() - 1) { // "Выйти" tab
                mainFrame.switchToLoginRegisterPanel();
            }
        });
        
        add(tabbedPane, BorderLayout.CENTER);
    }
}