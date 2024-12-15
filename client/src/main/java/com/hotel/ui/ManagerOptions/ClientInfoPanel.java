package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class ClientInfoPanel extends JPanel {
    public ClientInfoPanel() {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Информация о клиентах", new ManagerClientInfoPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}