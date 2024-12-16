package com.hotel.ui.ClientOptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ClientRoomBookingPanel extends JPanel {
    private JPanel roomGridPanel;
    private JPanel roomDetailsPanel;
    private JTextField searchField;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private int currentPage = 0;
    private int roomsPerPage = 9;
    private List<JButton> roomButtons;
    private JLabel photoLabel;
    private int currentPhotoIndex = 0;
    private List<String> photoPaths;
    public ClientRoomBookingPanel() {
        setLayout(new BorderLayout());

        // Панель поиска
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        JButton searchButton = new JButton("Поиск");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        add(searchPanel, BorderLayout.NORTH);

        // Панель сетки номеров
        roomGridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(roomGridPanel);
        JPanel gridPanelContainer = new JPanel(new BorderLayout());
        gridPanelContainer.add(scrollPane, BorderLayout.CENTER);

        // Кнопки для листания страниц
        JPanel paginationPanel = new JPanel(new FlowLayout());
        prevPageButton = new JButton("Предыдущая");
        nextPageButton = new JButton("Следующая");
        prevPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 0) {
                    currentPage--;
                    updateRoomGrid();
                }
            }
        });
        nextPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((currentPage + 1) * roomsPerPage < roomButtons.size()) {
                    currentPage++;
                    updateRoomGrid();
                }
            }
        });
        paginationPanel.add(prevPageButton);
        paginationPanel.add(nextPageButton);
        gridPanelContainer.add(paginationPanel, BorderLayout.SOUTH);

        add(gridPanelContainer, BorderLayout.CENTER);

        // Панель деталей номера
        roomDetailsPanel = new JPanel(new BorderLayout());
        roomDetailsPanel.setPreferredSize(new Dimension(300, 0));
        roomDetailsPanel.setBorder(BorderFactory.createTitledBorder("Информация о номере"));
        add(roomDetailsPanel, BorderLayout.WEST);

        // Заполнение сетки номеров (пример)
        roomButtons = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            JButton roomButton = new JButton("Номер " + i);
            roomButton.addActionListener(new RoomButtonListener(i));
            roomButtons.add(roomButton);
        }
        updateRoomGrid();
    }

    private void updateRoomGrid() {
        roomGridPanel.removeAll();
        int start = currentPage * roomsPerPage;
        int end = Math.min(start + roomsPerPage, roomButtons.size());
        for (int i = start; i < end; i++) {
            roomGridPanel.add(roomButtons.get(i));
        }
        roomGridPanel.revalidate();
        roomGridPanel.repaint();
    }

    private class RoomButtonListener implements ActionListener {
        private int roomNumber;

        public RoomButtonListener(int roomNumber) {
            this.roomNumber = roomNumber;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showRoomDetails(roomNumber);
        }
    }

    private void showRoomDetails(int roomNumber) {
        roomDetailsPanel.removeAll();

        // Панель для фото номера с кнопками перелистывания
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(200, 150));
        photoPanel.add(photoLabel, BorderLayout.CENTER);

        // Панель для кнопок перелистывания
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton prevPhotoButton = new JButton("<");
        JButton nextPhotoButton = new JButton(">");
        prevPhotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPhotoIndex > 0) {
                    currentPhotoIndex--;
                    updatePhoto();
                }
            }
        });
        nextPhotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPhotoIndex < photoPaths.size() - 1) {
                    currentPhotoIndex++;
                    updatePhoto();
                }
            }
        });
        buttonPanel.add(prevPhotoButton);
        buttonPanel.add(nextPhotoButton);
        photoPanel.add(buttonPanel, BorderLayout.SOUTH);
        photoPanel.setBorder(BorderFactory.createTitledBorder("Фото номера"));
        roomDetailsPanel.add(photoPanel, BorderLayout.NORTH);

        // Информация о номере
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.add(new JLabel("Номер: " + roomNumber));
        infoPanel.add(new JLabel("Тип: Тип " + roomNumber));
        infoPanel.add(new JLabel("Цена: " + (roomNumber * 1000) + " руб."));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Информация"));
        roomDetailsPanel.add(infoPanel, BorderLayout.CENTER);

        // Кнопка бронирования
        JButton bookButton = new JButton("Забронировать");
        roomDetailsPanel.add(bookButton, BorderLayout.SOUTH);

        // Пример путей к фото номеров
        photoPaths = new ArrayList<>();
        if (roomNumber == 1) {
            photoPaths.add("server\\src\\main\\resources\\images\\img.jpg");
            photoPaths.add("server\\src\\main\\resources\\images\\room1_2.jpg");
        } else {
            for (int i = 1; i <= 3; i++) {
                photoPaths.add("path/to/photo" + roomNumber + "_" + i + ".jpg");
            }
        }
        currentPhotoIndex = 0;
        updatePhoto();

        roomDetailsPanel.revalidate();
        roomDetailsPanel.repaint();
    }

    private void updatePhoto() {
            if (!photoPaths.isEmpty()) {
            try {
                // Загружаем изображение
                File imageFile = new File(photoPaths.get(currentPhotoIndex));
                if (!imageFile.exists()) {
                    photoLabel.setIcon(null);
                    photoLabel.setText("Изображение не найдено");
                    return;
                }
                
                BufferedImage img = ImageIO.read(imageFile);
                if (img == null) {
                    photoLabel.setIcon(null);
                    photoLabel.setText("Ошибка загрузки изображения");
                    return;
                }
                
                // Масштабируем изображение до размеров photoLabel
                int labelWidth = photoLabel.getWidth();
                int labelHeight = photoLabel.getHeight();
                if (labelWidth <= 0) labelWidth = 200;
                if (labelHeight <= 0) labelHeight = 150;
                
                Image scaledImg = img.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaledImg));
                photoLabel.setText(""); // Очищаем текст ошибки, если он был
                
            } catch (IOException e) {
                photoLabel.setIcon(null);
                photoLabel.setText("Ошибка: " + e.getMessage());
                e.printStackTrace();
            }
            } else {
            photoLabel.setIcon(null);
            photoLabel.setText("Нет доступных фотографий");
            }
        }
}