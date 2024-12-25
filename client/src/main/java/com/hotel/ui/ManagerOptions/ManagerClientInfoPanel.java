package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.hotel.client.HotelApiClient;
import com.hotel.model.User;

public class ManagerClientInfoPanel extends JPanel {
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private HotelApiClient apiClient;

    public ManagerClientInfoPanel() {
    //     apiClient = new HotelApiClient();
    //     setLayout(new BorderLayout());

    //     // Initialize table model with column names
    //     String[] columnNames = {"ID", "Имя", "Email", "Телефон"};
    //     tableModel = new DefaultTableModel(columnNames, 0) {
    //         @Override
    //         public boolean isCellEditable(int row, int column) {
    //             return column != 0; // Make all columns editable except ID
    //         }
    //     };

    //     // Initialize JTable with the model
    //     clientTable = new JTable(tableModel);
    //     JScrollPane scrollPane = new JScrollPane(clientTable);
    //     add(scrollPane, BorderLayout.CENTER);

    //     // Load client data
    //     loadClientData();

    //     // Add buttons panel
    //     JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    //     JButton saveButton = new JButton("Сохранить изменения");
    //     JButton refreshButton = new JButton("Обновить данные");
    //     buttonPanel.add(refreshButton);
    //     buttonPanel.add(saveButton);
    //     add(buttonPanel, BorderLayout.SOUTH);

    //     // Action listeners
    //     refreshButton.addActionListener(e -> loadClientData());
    //     saveButton.addActionListener(e -> saveClientData());
    // }

    // private void loadClientData() {
    //     try {
    //         List<User> clients = apiClient.getAllUsers(); // Assuming this method exists
    //         tableModel.setRowCount(0); // Clear existing data
    //         for (User client : clients) {
    //             Object[] row = {client.getId(), client.getName(), client.getEmail(), client.getPhone()};
    //             tableModel.addRow(row);
    //         }
    //     } catch (Exception e) {
    //         JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных клиентов: " + e.getMessage());
    //     }
    // }

    // private void saveClientData() {
    //     try {
    //         int rowCount = tableModel.getRowCount();
    //         for (int i = 0; i < rowCount; i++) {
    //             Long id = Long.parseLong(tableModel.getValueAt(i, 0).toString());
    //             String name = tableModel.getValueAt(i, 1).toString();
    //             String email = tableModel.getValueAt(i, 2).toString();
    //             String phone = tableModel.getValueAt(i, 3).toString();

    //             apiClient.updateUser(id, name, email, phone); // Assuming this method exists
    //         }
    //         JOptionPane.showMessageDialog(this, "Данные клиентов успешно сохранены.");
    //     } catch (Exception e) {
    //         JOptionPane.showMessageDialog(this, "Ошибка при сохранении данных клиентов: " + e.getMessage());
    //     }
    }
}
