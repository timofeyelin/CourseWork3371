package com.hotel.ui.Panels;

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
        tabbedPane.addTab("Информация о клиентах", new ClientInfoPanel());

        // Добавляем вкладку "Выйти"
        tabbedPane.addTab("Выйти", new JPanel());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == tabbedPane.getTabCount() - 1) {
                mainFrame.switchToLoginRegisterPanel();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }
}