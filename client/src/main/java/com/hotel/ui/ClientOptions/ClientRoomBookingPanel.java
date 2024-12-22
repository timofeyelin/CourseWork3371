package com.hotel.ui.ClientOptions;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.hotel.client.HotelApiClient;
import com.hotel.dto.RoomDTO;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.time.LocalDate;

public class ClientRoomBookingPanel extends JPanel {
    private JPanel roomGridPanel;
    private JTextField searchField;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private int currentPage = 0;
    private int roomsPerPage = 9;
    private List<RoomFrame> roomFrames;
    private HotelApiClient apiClient;

    public ClientRoomBookingPanel() {
        apiClient = new HotelApiClient();
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
        prevPageButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateRoomGrid();
            }
        });
        nextPageButton.addActionListener(e -> {
            if ((currentPage + 1) * roomsPerPage < roomFrames.size()) {
                currentPage++;
                updateRoomGrid();
            }
        });
        paginationPanel.add(prevPageButton);
        paginationPanel.add(nextPageButton);
        gridPanelContainer.add(paginationPanel, BorderLayout.SOUTH);

        add(gridPanelContainer, BorderLayout.CENTER);

        loadRoomsFromDatabase();
    }

    private void loadRoomsFromDatabase() {
        try {
            List<RoomDTO> rooms = apiClient.getAvailableRooms();
            roomFrames = new ArrayList<>();
            if (rooms != null) {
                for (RoomDTO room : rooms) {
                    roomFrames.add(new RoomFrame(room));
                }
            }
            updateRoomGrid();
        } catch (Exception e) {
            e.printStackTrace(); // Add stack trace for debugging
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке номеров: " + e.getMessage());
        }
    }

    public void refreshRoomGrid() {
        loadRoomsFromDatabase();
    }

    private void updateRoomGrid() {
        roomGridPanel.removeAll();
        int start = currentPage * roomsPerPage;
        int end = Math.min(start + roomsPerPage, roomFrames.size());
        for (int i = start; i < end; i++) {
            roomGridPanel.add(roomFrames.get(i));
        }
        roomGridPanel.revalidate();
        roomGridPanel.repaint();
    }

    private class RoomFrame extends JPanel {
        private final RoomDTO room;
        private final HotelApiClient apiClient;
        private JLabel photoLabel;
        private JButton prevButton;
        private JButton nextButton;
        private JButton bookButton;
        private int currentPhotoIndex = 0;
        private List<String> photoPaths;
        private String roomNumber;
        private JLabel roomNumberLabel;
        private JLabel roomTypeLabel;
        private JLabel roomPriceLabel;
        private JLabel roomDescriptionLabel; // Add new field

        public RoomFrame(RoomDTO room) {
            this.room = room;
            this.apiClient = ClientRoomBookingPanel.this.apiClient;
            this.roomNumber = room.getNumber();
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createEtchedBorder());

            setupPhotoPanel();
            setupInfoPanel();
            initializePhotos();
            addPhotoNavigationListeners();
        }

        private void setupPhotoPanel() {
            JPanel photoPanel = new JPanel(new BorderLayout());
            photoLabel = new JLabel();
            photoLabel.setPreferredSize(new Dimension(200, 150));
            photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            photoLabel.setBorder(BorderFactory.createEtchedBorder());
            photoPanel.add(photoLabel, BorderLayout.CENTER);
            add(photoPanel, BorderLayout.CENTER);
        }

        private void setupInfoPanel() {
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            
            roomNumberLabel = new JLabel("Номер: " + room.getNumber(), SwingConstants.CENTER);
            roomTypeLabel = new JLabel("Тип: " + room.getType(), SwingConstants.CENTER);
            roomPriceLabel = new JLabel("Цена: " + room.getPrice() + " руб.", SwingConstants.CENTER);
            roomDescriptionLabel = new JLabel("Описание: " + room.getDescription(), SwingConstants.CENTER);
            
            // Center text alignment
            roomNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roomTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roomPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roomDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add navigation and book buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            prevButton = new JButton("<<");
            bookButton = new JButton("Забронировать");
            nextButton = new JButton(">>");
            
            buttonPanel.add(prevButton);
            buttonPanel.add(bookButton);
            buttonPanel.add(nextButton);
            buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            infoPanel.add(roomNumberLabel);
            infoPanel.add(roomTypeLabel);
            infoPanel.add(roomPriceLabel);
            infoPanel.add(roomDescriptionLabel);
            infoPanel.add(buttonPanel);
            
            add(infoPanel, BorderLayout.SOUTH);

            bookButton.addActionListener(e -> handleBooking());
        }

        private void handleBooking() {
            JDialog bookingDialog = new JDialog();
            bookingDialog.setTitle("Бронирование номера");
            bookingDialog.setLayout(new BorderLayout());
            bookingDialog.setModal(true);

            JPanel bookingForm = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);
            gbc.anchor = GridBagConstraints.WEST;

            // Start date
            gbc.gridx = 0;
            gbc.gridy = 0;
            bookingForm.add(new JLabel("Дата начала (YYYY-MM-DD):"), gbc);

            gbc.gridx = 1;
            JTextField startDateField = new JTextField(15);
            bookingForm.add(startDateField, gbc);

            // End date
            gbc.gridx = 0;
            gbc.gridy = 1;
            bookingForm.add(new JLabel("Дата конца (YYYY-MM-DD):"), gbc);

            gbc.gridx = 1;
            JTextField endDateField = new JTextField(15);
            bookingForm.add(endDateField, gbc);

            // Confirm button
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            JButton confirmButton = new JButton("Подтвердить бронирование");
            bookingForm.add(confirmButton, gbc);

            confirmButton.addActionListener(e -> {
                try {
                    Long userId = HotelApiClient.getCurrentUserId();
                    if (userId != null) {
                        apiClient.createBooking(room.getNumber(), userId, 
                            startDateField.getText(), 
                            endDateField.getText());
                        JOptionPane.showMessageDialog(bookingDialog, "Номер успешно забронирован");
                        bookingDialog.dispose();
                        loadRoomsFromDatabase();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(bookingDialog, 
                        "Ошибка при бронировании: " + ex.getMessage());
                }
            });

            bookingDialog.add(bookingForm, BorderLayout.CENTER);
            bookingDialog.pack();
            bookingDialog.setLocationRelativeTo(this);
            bookingDialog.setVisible(true);
        }

        private void initializePhotos() {
            photoPaths = new ArrayList<>();
            String basePath = "server\\src\\main\\resources\\images\\";
            // Get photos from room data or use defaults
            List<String> roomPhotos = room.getPhotos();
            if (roomPhotos != null && !roomPhotos.isEmpty()) {
                photoPaths.addAll(roomPhotos);
            } else {
                photoPaths.add(basePath + "default_room.jpg");
            }
            updatePhotoButton();
        }

        private void addPhotoNavigationListeners() {
            prevButton.addActionListener(e -> {
                currentPhotoIndex = (currentPhotoIndex - 1 + photoPaths.size()) % photoPaths.size();
                updatePhotoButton();
            });

            nextButton.addActionListener(e -> {
                currentPhotoIndex = (currentPhotoIndex + 1) % photoPaths.size();
                updatePhotoButton();
            });
        }

        private void updatePhotoButton() {
            if (!photoPaths.isEmpty()) {
                try {
                    File imageFile = new File(photoPaths.get(currentPhotoIndex));
                    if (!imageFile.exists()) {
                        photoLabel.setIcon(null);
                        photoLabel.setText("Номер " + roomNumber);
                        return;
                    }
                    BufferedImage img = ImageIO.read(imageFile);
                    if (img == null) {
                        photoLabel.setIcon(null);
                        photoLabel.setText("Номер " + roomNumber);
                        return;
                    }
                    Image scaledImg = img.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(scaledImg));
                    photoLabel.setText("");
                } catch (IOException e) {
                    photoLabel.setIcon(null);
                    photoLabel.setText("Номер " + roomNumber);
                }
            }
        }
    }
}