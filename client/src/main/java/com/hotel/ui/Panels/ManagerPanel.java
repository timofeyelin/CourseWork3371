package com.hotel.ui.Panels;

import com.hotel.ui.ManagerOptions.ManagerClientInfoPanel;
import com.hotel.ui.ManagerOptions.RoomsPanel;
import com.hotel.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class ManagerPanel extends JPanel {
    public ManagerPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        ManagerClientInfoPanel clientInfoPanel = new ManagerClientInfoPanel();
        tabbedPane.addTab("Управление номерами", new RoomsPanel());
        tabbedPane.addTab("Информация о клиентах", clientInfoPanel);

        // Добавляем вкладку "Выйти"
        tabbedPane.addTab("Выйти", new JPanel());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == tabbedPane.getTabCount() - 1) {
                mainFrame.switchToLoginRegisterPanel();
            }
            else if (tabbedPane.getSelectedIndex() == 1) {
                clientInfoPanel.refreshTableData();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }
}