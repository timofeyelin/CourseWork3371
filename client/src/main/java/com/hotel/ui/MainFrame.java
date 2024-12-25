package com.hotel.ui;

import javax.swing.*;
import com.hotel.ui.Panels.LoginRegisterPanel;
import com.hotel.ui.Panels.ManagerPanel;
import com.hotel.ui.Panels.ClientPanel;
import com.hotel.ui.Panels.AdminPanel;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Hotel Booking System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    public void initUI() {
        LoginRegisterPanel loginRegisterPanel = new LoginRegisterPanel(MainFrame.this);
        add(loginRegisterPanel);
    }

    public void switchToUserView(String role) {
        getContentPane().removeAll();

        if ("ADMIN".equals(role)) {
            add(new AdminPanel(MainFrame.this));
        } else if ("MANAGER".equals(role)) {
            add(new ManagerPanel(MainFrame.this));
        } else {
            add(new ClientPanel(MainFrame.this));
        }

        revalidate();
        repaint();
    }

    public void switchToLoginRegisterPanel() {
        getContentPane().removeAll();
        initUI();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}