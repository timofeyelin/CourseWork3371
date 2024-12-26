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
            if (selectedIndex == 1) {
                roomBookingPanel.refreshRoomGrid();
            } else if (selectedIndex == 0) {
                myBookingsPanel.refreshRoomGrid();
            } else if (selectedIndex == tabbedPane.getTabCount() - 1) {
                mainFrame.switchToLoginRegisterPanel();
            }
        });
        
        add(tabbedPane, BorderLayout.CENTER);
    }
}