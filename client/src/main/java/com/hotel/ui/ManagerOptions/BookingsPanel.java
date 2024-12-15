package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class BookingsPanel extends JPanel {
    public BookingsPanel() {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Посмотреть бронирования", new ManagerViewBookingsPanel());
        tabbedPane.addTab("Отредактировать бронирование", new ManagerEditBookingPanel());
        tabbedPane.addTab("Удалить бронирование", new ManagerDeleteBookingPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}