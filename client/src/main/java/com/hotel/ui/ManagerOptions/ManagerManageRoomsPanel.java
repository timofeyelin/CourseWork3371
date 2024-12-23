package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import com.hotel.client.HotelApiClient;
import com.hotel.dto.RoomDTO;

public class ManagerManageRoomsPanel extends JPanel {
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private List<String> selectedPhotoPaths;
    private JList<String> photoList;
    private DefaultListModel<String> photoListModel;

    public ManagerManageRoomsPanel() {
        setLayout(new BorderLayout());
        selectedPhotoPaths = new ArrayList<>();

        // Create main panels
        JPanel operationsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        JPanel addRoomPanel = createAddRoomPanel();
        JPanel editRoomPanel = createEditRoomPanel();
        JPanel deleteRoomPanel = createDeleteRoomPanel();
        
        operationsPanel.add(addRoomPanel);
        operationsPanel.add(editRoomPanel);
        operationsPanel.add(deleteRoomPanel);

        // Create table panel
        String[] columnNames = {"ID", "Номер", "Тип", "Цена", "Описание"};
        tableModel = new DefaultTableModel(columnNames, 0);
        roomsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(roomsTable);

        // Create photo panel
        JPanel photoPanel = createPhotoPanel();

        // Layout assembly
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(photoPanel, BorderLayout.CENTER);
        southPanel.add(operationsPanel, BorderLayout.SOUTH);

        add(tableScrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        loadAvailableRooms();
    }

    private JPanel createAddRoomPanel() {
        JPanel addRoomPanel = new JPanel(new GridBagLayout());
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

        addButton.addActionListener(e -> {
            String number = addNumberField.getText();
            String type = addTypeField.getText();
            String priceText = addPriceField.getText();
            String description = addDescriptionArea.getText();
            try {
                BigDecimal price = new BigDecimal(priceText);
                HotelApiClient apiClient = new HotelApiClient();
                apiClient.addRoom(number, type, price, description, selectedPhotoPaths);
                JOptionPane.showMessageDialog(this, "Номер успешно добавлен");
                loadAvailableRooms(); // Обновить таблицу после добавления номера
                clearPhotoList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении номера: " + ex.getMessage());
            }
        });

        return addRoomPanel;
    }

    private JPanel createEditRoomPanel() {
        JPanel editRoomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

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
                clearPhotoList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении номера: " + ex.getMessage());
            }
        });

        return editRoomPanel;
    }

    private JPanel createDeleteRoomPanel() {
        JPanel deleteRoomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

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

        return deleteRoomPanel;
    }

    private JPanel createPhotoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Фотографии номера"));

        photoListModel = new DefaultListModel<>();
        photoList = new JList<>(photoListModel);
        photoList.setPreferredSize(new Dimension(200, 150));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton addPhotoButton = new JButton("Добавить фото");
        JButton removePhotoButton = new JButton("Удалить фото");

        addPhotoButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    String targetPath = "server/src/main/resources/images/" + file.getName();
                    selectedPhotoPaths.add(targetPath);
                    photoListModel.addElement(file.getName());
                    try {
                        File targetFile = new File(targetPath);
                        targetFile.getParentFile().mkdirs();
                        Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this,
                            "Ошибка при копировании файла: " + ex.getMessage());
                    }
                }
            }
        });

        removePhotoButton.addActionListener(e -> {
            int selectedIndex = photoList.getSelectedIndex();
            if (selectedIndex != -1) {
                selectedPhotoPaths.remove(selectedIndex);
                photoListModel.remove(selectedIndex);
            }
        });

        buttonPanel.add(addPhotoButton);
        buttonPanel.add(removePhotoButton);

        panel.add(new JScrollPane(photoList), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void clearPhotoList() {
        photoListModel.clear();
        selectedPhotoPaths.clear();
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