package com.hotel.ui.ManagerOptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;

import com.hotel.client.HotelApiClient;
import com.hotel.dto.RoomDTO;

public class ManagerAddRoomPanel extends JPanel {
    private final JTextField numberField;
    private final JTextField typeField;
    private final JTextField priceField;
    private final JTextArea descriptionArea;
    private final DefaultListModel<String> photoListModel;
    private final JList<String> photoList;
    private final List<String> selectedPhotoPaths;
    private Runnable updateCallback;
    private JLabel photoLabel;
    private int currentPhotoIndex = 0;

    public ManagerAddRoomPanel() {
        this.selectedPhotoPaths = new ArrayList<>();
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Добавить новый номер", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JPanel addRoomForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        addRoomForm.add(new JLabel("Номер комнаты:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        numberField = new JTextField(15);
        addRoomForm.add(numberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addRoomForm.add(new JLabel("Тип номера:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        typeField = new JTextField(15);
        addRoomForm.add(typeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addRoomForm.add(new JLabel("Цена:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        priceField = new JTextField(15);
        addRoomForm.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addRoomForm.add(new JLabel("Описание:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        descriptionArea = new JTextArea(5, 15);
        addRoomForm.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Добавить номер");
        addRoomForm.add(addButton, gbc);

        add(addRoomForm, BorderLayout.CENTER);

        // Панель для управления фотографиями
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setBorder(BorderFactory.createTitledBorder("Фотографии"));

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
                    photoListModel.addElement(targetPath);
                    try {
                        File targetFile = new File(targetPath);
                        targetFile.getParentFile().mkdirs();
                        Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Ошибка при копировании файла: " + ex.getMessage());
                    }
                }
                updatePhoto(); // Обновить фото после добавления
            }
        });

        removePhotoButton.addActionListener(e -> {
            int selectedIndex = photoList.getSelectedIndex();
            if (selectedIndex != -1) {
                selectedPhotoPaths.remove(selectedIndex);
                photoListModel.remove(selectedIndex);
                updatePhoto(); // Обновить фото после удаления
            }
        });

        buttonPanel.add(addPhotoButton);
        buttonPanel.add(removePhotoButton);

        photoPanel.add(new JScrollPane(photoList), BorderLayout.CENTER);
        photoPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Панель для отображения фотографий
        JPanel displayPhotoPanel = new JPanel(new BorderLayout());
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(200, 150));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setBorder(BorderFactory.createEtchedBorder());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");

        prevButton.addActionListener(e -> {
            currentPhotoIndex = (currentPhotoIndex - 1 + selectedPhotoPaths.size()) % selectedPhotoPaths.size();
            updatePhoto();
        });

        nextButton.addActionListener(e -> {
            currentPhotoIndex = (currentPhotoIndex + 1) % selectedPhotoPaths.size();
            updatePhoto();
        });

        navPanel.add(prevButton);
        navPanel.add(nextButton);

        displayPhotoPanel.add(photoLabel, BorderLayout.CENTER);
        displayPhotoPanel.add(navPanel, BorderLayout.SOUTH);

        photoPanel.add(displayPhotoPanel, BorderLayout.NORTH);

        add(photoPanel, BorderLayout.EAST);

        addButton.addActionListener(e -> handleAddRoom());

        updatePhoto();
    }

    public void setUpdateCallback(Runnable updateCallback) {
        this.updateCallback = updateCallback;
    }

    private void handleAddRoom() {
        String number = numberField.getText();
        String type = typeField.getText();
        String priceText = priceField.getText();
        String description = descriptionArea.getText();
        try {
            BigDecimal price = new BigDecimal(priceText);
            HotelApiClient apiClient = new HotelApiClient();
            apiClient.addRoom(number, type, price, description, selectedPhotoPaths);
            JOptionPane.showMessageDialog(this, "Номер успешно добавлен");
            if (updateCallback != null) {
                updateCallback.run();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении номера: " + ex.getMessage());
        }
    }

    private void updatePhoto() {
        if (!selectedPhotoPaths.isEmpty()) {
            String photoPath = selectedPhotoPaths.get(currentPhotoIndex);
            try {
                BufferedImage img = ImageIO.read(new File(photoPath));
                if (img != null) {
                    Image scaledImg = img.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(scaledImg));
                    photoLabel.setText("");
                } else {
                    photoLabel.setIcon(null);
                    photoLabel.setText("Нет изображения");
                }
            } catch (IOException e) {
                photoLabel.setIcon(null);
                photoLabel.setText("Ошибка загрузки");
            }
        } else {
            photoLabel.setIcon(null);
            photoLabel.setText("Нет изображений");
        }
    }
}