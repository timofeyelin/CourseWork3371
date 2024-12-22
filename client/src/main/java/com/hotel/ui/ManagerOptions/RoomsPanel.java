package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;

public class RoomsPanel extends JPanel {
    public RoomsPanel() {
        setLayout(new BorderLayout());

        add(new ManagerManageRoomsPanel(), BorderLayout.CENTER);
    }
}