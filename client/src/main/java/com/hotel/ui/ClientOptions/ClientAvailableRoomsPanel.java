package com.hotel.ui.ClientOptions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.hotel.client.HotelApiClient;
import com.hotel.dto.RoomDTO;
import java.util.List;

public class ClientAvailableRoomsPanel extends JPanel {
    private JTable roomsTable;
    private DefaultTableModel tableModel;

    public ClientAvailableRoomsPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Доступные номера", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Номер", "Тип", "Цена", "Описание"};
        tableModel = new DefaultTableModel(columnNames, 0);
        roomsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> loadAvailableRooms());
        add(refreshButton, BorderLayout.SOUTH);

        loadAvailableRooms();
    }

    private void loadAvailableRooms() {
        SwingUtilities.invokeLater(() -> {
            try {
                HotelApiClient apiClient = new HotelApiClient();
                List<RoomDTO> rooms = apiClient.getAvailableRooms();
                tableModel.setRowCount(0);
                for (RoomDTO room : rooms) {
                    Object[] row = {
                            room.getId(),
                            room.getNumber(),
                            room.getType(),
                            room.getPrice(),
                            room.getDescription()
                    };
                    tableModel.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при загрузке номеров: " + ex.getMessage());
            }
        });
    }
}