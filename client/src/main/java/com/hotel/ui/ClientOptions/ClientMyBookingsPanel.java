package com.hotel.ui.ClientOptions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.hotel.client.HotelApiClient;
import com.hotel.model.Booking;

public class ClientMyBookingsPanel extends JPanel {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;

    public ClientMyBookingsPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Мои бронирования", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Номер комнаты");
        tableModel.addColumn("Дата начала");
        tableModel.addColumn("Дата конца");

        bookingsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Обновить");
        add(refreshButton, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadBookings());
        loadBookings();
    }

    private void loadBookings() {
        try {
            Long userId = HotelApiClient.getCurrentUserId(); // Получение текущего userId
            HotelApiClient apiClient = new HotelApiClient();
            List<Booking> bookings = apiClient.getUserBookings(userId);
    
            tableModel.setRowCount(0);
            for (Booking booking : bookings) {
                tableModel.addRow(new Object[]{
                        booking.getId(),
                        booking.getRoom().getNumber(),
                        booking.getStartDate(),
                        booking.getEndDate()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке бронирований: " + ex.getMessage());
        }
    }
}