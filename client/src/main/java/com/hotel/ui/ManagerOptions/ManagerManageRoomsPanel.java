package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import com.hotel.client.HotelApiClient;
import com.hotel.dto.RoomDTO;

public class ManagerManageRoomsPanel extends JPanel {
    private JTable roomsTable;
    private DefaultTableModel tableModel;

    public ManagerManageRoomsPanel() {
        setLayout(new BorderLayout());

        JPanel addRoomPanel = new JPanel(new GridBagLayout());
        JPanel editRoomPanel = new JPanel(new GridBagLayout());
        JPanel deleteRoomPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Добавить номер
        gbc.gridx = 0;
        gbc.gridy = 0;
        addRoomPanel.add(new JLabel("Добавить номер"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addRoomPanel.add(new JLabel("Номер:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField addNumberField = new JTextField(15);
        addRoomPanel.add(addNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addRoomPanel.add(new JLabel("Тип номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField addTypeField = new JTextField(15);
        addRoomPanel.add(addTypeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addRoomPanel.add(new JLabel("Цена:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField addPriceField = new JTextField(15);
        addRoomPanel.add(addPriceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addRoomPanel.add(new JLabel("Описание:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextArea addDescriptionArea = new JTextArea(5, 15);
        addRoomPanel.add(new JScrollPane(addDescriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Добавить номер");
        addRoomPanel.add(addButton, gbc);

        // Отредактировать номер
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        editRoomPanel.add(new JLabel("Отредактировать номер"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        editRoomPanel.add(new JLabel("Номер комнаты:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField editNumberField = new JTextField(15);
        editRoomPanel.add(editNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        editRoomPanel.add(new JLabel("Новый тип номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField editTypeField = new JTextField(15);
        editRoomPanel.add(editTypeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        editRoomPanel.add(new JLabel("Новая цена:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField editPriceField = new JTextField(15);
        editRoomPanel.add(editPriceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        editRoomPanel.add(new JLabel("Новое описание:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextArea editDescriptionArea = new JTextArea(5, 15);
        editRoomPanel.add(new JScrollPane(editDescriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JCheckBox keepDescriptionCheckBox = new JCheckBox("Оставить прежнее описание");
        editRoomPanel.add(keepDescriptionCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton editButton = new JButton("Редактировать номер");
        editRoomPanel.add(editButton, gbc);

        // Удалить номер
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        deleteRoomPanel.add(new JLabel("Удалить номер"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        deleteRoomPanel.add(new JLabel("Номер комнаты:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField deleteNumberField = new JTextField(15);
        deleteRoomPanel.add(deleteNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton deleteButton = new JButton("Удалить номер");
        deleteRoomPanel.add(deleteButton, gbc);

        // Добавляем панели в основной макет
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(addRoomPanel, BorderLayout.WEST);
        mainPanel.add(editRoomPanel, BorderLayout.CENTER);
        mainPanel.add(deleteRoomPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Таблица с доступными номерами
        String[] columnNames = {"ID", "Номер", "Тип", "Цена", "Описание"};
        tableModel = new DefaultTableModel(columnNames, 0);
        roomsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        add(scrollPane, BorderLayout.NORTH);

        // Action listeners
        addButton.addActionListener(e -> {
            String number = addNumberField.getText();
            String type = addTypeField.getText();
            String priceText = addPriceField.getText();
            String description = addDescriptionArea.getText();
            try {
                BigDecimal price = new BigDecimal(priceText);
                HotelApiClient apiClient = new HotelApiClient();
                apiClient.addRoom(number, type, price, description);
                JOptionPane.showMessageDialog(this, "Номер успешно добавлен");
                loadAvailableRooms(); // Обновить таблицу после добавления номера
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении номера: " + ex.getMessage());
            }
        });

        editButton.addActionListener(e -> {
            String roomNumber = editNumberField.getText();
            String newType = editTypeField.getText();
            String priceText = editPriceField.getText();
            String description = editDescriptionArea.getText();
            boolean keepDescription = keepDescriptionCheckBox.isSelected();

            try {
                BigDecimal newPrice = new BigDecimal(priceText);
                HotelApiClient apiClient = new HotelApiClient();
                apiClient.editRoomByNumber(roomNumber, newType, newPrice, keepDescription ? null : description);
                JOptionPane.showMessageDialog(this, "Номер успешно обновлен");
                loadAvailableRooms(); // Обновить таблицу после редактирования номера
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении номера: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            String roomNumber = deleteNumberField.getText();

            try {
                HotelApiClient apiClient = new HotelApiClient();
                apiClient.deleteRoomByNumber(roomNumber);
                JOptionPane.showMessageDialog(this, "Номер успешно удален");
                loadAvailableRooms(); // Обновить таблицу после удаления номера
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении номера: " + ex.getMessage());
            }
        });

        // Загрузить доступные номера при инициализации
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