package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;

import com.hotel.client.HotelApiClient;
import com.hotel.model.Room;

public class ManagerAddRoomPanel extends JPanel {
    private List<String> selectedPhotoPaths;
    private JList<String> photoList;
    private DefaultListModel<String> photoListModel;

    public ManagerAddRoomPanel() {
        setLayout(new BorderLayout());
        selectedPhotoPaths = new ArrayList<>();
        
        JLabel label = new JLabel("Добавить новый номер", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Форма для данных номера
        JPanel addRoomForm = createRoomDetailsPanel(gbc);
        
        // Панель для управления фотографиями
        JPanel photoPanel = createPhotoPanel(gbc);

        // Размещаем панели
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(addRoomForm, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(photoPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createRoomDetailsPanel(GridBagConstraints gbc) {
        JPanel panel = new JPanel(new GridBagLayout());
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Номер:"), gbc);

        gbc.gridx = 1;
        JTextField numberField = new JTextField(15);
        panel.add(numberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Тип номера:"), gbc);

        gbc.gridx = 1;
        JTextField typeField = new JTextField(15);
        panel.add(typeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Цена:"), gbc);

        gbc.gridx = 1;
        JTextField priceField = new JTextField(15);
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Описание:"), gbc);

        gbc.gridx = 1;
        JTextArea descriptionArea = new JTextArea(5, 15);
        panel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Добавить номер");
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            String number = numberField.getText();
            String type = typeField.getText();
            String priceText = priceField.getText();
            String description = descriptionArea.getText();
            try {
                BigDecimal price = new BigDecimal(priceText);
                Room room = new Room();
                room.setNumber(number);
                room.setType(type);
                room.setPrice(price);
                room.setDescription(description);
                room.setPhotos(selectedPhotoPaths);
                
                HotelApiClient apiClient = new HotelApiClient();
                apiClient.addRoom(number, type, price, description, selectedPhotoPaths);
                JOptionPane.showMessageDialog(this, "Номер успешно добавлен");
                
                // Очистка полей после успешного добавления
                numberField.setText("");
                typeField.setText("");
                priceField.setText("");
                descriptionArea.setText("");
                photoListModel.clear();
                selectedPhotoPaths.clear();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении номера: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createPhotoPanel(GridBagConstraints gbc) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Фотографии"));

        // Модель списка фотографий
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
                        Files.copy(file.toPath(), targetFile.toPath(), 
                            StandardCopyOption.REPLACE_EXISTING);
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
}