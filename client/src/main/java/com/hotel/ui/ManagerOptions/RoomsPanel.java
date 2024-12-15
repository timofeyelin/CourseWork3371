package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class RoomsPanel extends JPanel {
    public RoomsPanel() {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Добавить номер", new ManagerAddRoomPanel());
        tabbedPane.addTab("Отредактировать номер", new ManagerEditRoomPanel());
        tabbedPane.addTab("Удалить номер", new ManagerDeleteRoomPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}