package com.hotel.ui.ClientOptions;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.hotel.client.HotelApiClient;
import com.hotel.dto.RoomDTO;
import com.hotel.utils.DateUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

public class ClientRoomBookingPanel extends JPanel {
    private JPanel roomGridPanel;
    private JTextField searchField;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private int currentPage = 0;
    private int roomsPerPage = 3;
    private List<RoomFrame> roomFrames;
    private HotelApiClient apiClient;
    private JLabel pageNumberLabel;

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
        roomGridPanel = new JPanel(new GridLayout(1, 3, 10, 10));
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
        pageNumberLabel = new JLabel("Страница 1");
        paginationPanel.add(prevPageButton);
        paginationPanel.add(pageNumberLabel);
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
        
        // Update page number label
        int totalPages = (int) Math.ceil((double) roomFrames.size() / roomsPerPage);
        pageNumberLabel.setText(String.format("Страница %d из %d", currentPage + 1, totalPages));
        
        roomGridPanel.revalidate();
        roomGridPanel.repaint();
    }

    public void centerDialog(JDialog dialog) {
        // Get parent window
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow != null) {
            // Center relative to parent window
            int x = parentWindow.getX() + (parentWindow.getWidth() - dialog.getWidth()) / 2;
            int y = parentWindow.getY() + (parentWindow.getHeight() - dialog.getHeight()) / 2;
            dialog.setLocation(x, y);
        } else {
            // If no parent, center on screen
            dialog.setLocationRelativeTo(null);
        }
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
            bookingForm.add(new JLabel("Дата начала (ДД.ММ.ГГГГ):"), gbc);

            gbc.gridx = 1;
            JTextField startDateField = new JTextField(15);
            bookingForm.add(startDateField, gbc);

            // End date
            gbc.gridx = 0;
            gbc.gridy = 1;
            bookingForm.add(new JLabel("Дата конца (ДД.ММ.ГГГГ):"), gbc);

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
                        String startDate = DateUtils.convertDateToServer(startDateField.getText());
                        String endDate = DateUtils.convertDateToServer(endDateField.getText());
                        
                        apiClient.createBooking(room.getNumber(), userId, startDate, endDate);
                        JOptionPane.showMessageDialog(bookingDialog, "Номер успешно забронирован");
                        bookingDialog.dispose();
                        loadRoomsFromDatabase();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(bookingDialog, 
                        "Ошибка при бронировании: Проверьте формат даты (ДД.ММ.ГГГГ)");
                }
            });

            bookingDialog.add(bookingForm, BorderLayout.CENTER);
            bookingDialog.pack();
            centerDialog(bookingDialog);
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
                photoPaths.add(basePath + "room1_1.jpg");
                photoPaths.add(basePath + "room1_2.jpg");
            }
            updatePhoto();
        }

        private void addPhotoNavigationListeners() {
            prevButton.addActionListener(e -> {
                currentPhotoIndex = (currentPhotoIndex - 1 + photoPaths.size()) % photoPaths.size();
                updatePhoto();
            });

            nextButton.addActionListener(e -> {
                currentPhotoIndex = (currentPhotoIndex + 1) % photoPaths.size();
                updatePhoto();
            });
        }

        private void updatePhoto() {
            if (!photoPaths.isEmpty()) {
                photoLabel.setIcon(null);
                photoLabel.setText("Загрузка...");
                
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() {
                        try {
                            File imageFile = new File(photoPaths.get(currentPhotoIndex));
                            if (!imageFile.exists()) {
                                return null;
                            }
                            BufferedImage img = ImageIO.read(imageFile);
                            if (img == null) {
                                return null;
                            }
                            Image scaledImg = img.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                            return new ImageIcon(scaledImg);
                        } catch (IOException e) {
                            return null;
                        }
                    }
        
                    @Override
                    protected void done() {
                        try {
                            ImageIcon icon = get();
                            if (icon != null) {
                                photoLabel.setIcon(icon);
                                photoLabel.setText("");
                            } else {
                                photoLabel.setIcon(null);
                                photoLabel.setText("Номер " + roomNumber);
                            }
                        } catch (Exception e) {
                            photoLabel.setIcon(null);
                            photoLabel.setText("Номер " + roomNumber);
                        }
                    }
                };
                
                worker.execute();
            }
        }
    }
}