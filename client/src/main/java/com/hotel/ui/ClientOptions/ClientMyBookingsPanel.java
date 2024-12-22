package com.hotel.ui.ClientOptions;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.hotel.client.HotelApiClient;
import com.hotel.model.Booking;
import com.hotel.utils.DateUtils;
import com.hotel.ui.ClientOptions.ClientRoomBookingPanel;

import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ClientMyBookingsPanel extends JPanel {
    private JPanel roomGridPanel;
    private JTextField searchField;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private int currentPage = 0;
    private int roomsPerPage = 3;
    private List<BookedRoomFrame> roomFrames;
    private HotelApiClient apiClient;
    private JLabel pageNumberLabel;

    public ClientMyBookingsPanel() {
        apiClient = new HotelApiClient();
        setLayout(new BorderLayout());

        // Панель сетки номеров
        roomGridPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(roomGridPanel);
        JPanel gridPanelContainer = new JPanel(new BorderLayout());
        gridPanelContainer.add(scrollPane, BorderLayout.CENTER);

         // Панель поиска
         JPanel searchPanel = new JPanel(new BorderLayout());
         searchField = new JTextField();
         JButton searchButton = new JButton("Поиск");
         searchPanel.add(searchField, BorderLayout.CENTER);
         searchPanel.add(searchButton, BorderLayout.EAST);
         add(searchPanel, BorderLayout.NORTH);

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

        loadBookings();
    }

    private void loadBookings() {
        try {
            Long userId = HotelApiClient.getCurrentUserId();
            List<Booking> bookings = apiClient.getUserBookings(userId);
            roomFrames = new ArrayList<>();
            for (Booking booking : bookings) {
                roomFrames.add(new BookedRoomFrame(booking));
            }
            updateRoomGrid();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке бронирований: " + e.getMessage());
        }
    }

    public void refreshRoomGrid() {
        loadBookings();
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

    private void showCenteredMessage(String message, String title, int messageType) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setModal(true);
        
        JOptionPane optionPane = new JOptionPane(
            message,
            messageType,
            JOptionPane.DEFAULT_OPTION
        );
        
        dialog.setContentPane(optionPane);
        
        optionPane.addPropertyChangeListener(e -> {
            String prop = e.getPropertyName();
            if (dialog.isVisible() && (e.getSource() == optionPane) && 
                (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                dialog.dispose();
            }
        });
        
        dialog.pack();
        ClientRoomBookingPanel clientRoomBookingPanel = new ClientRoomBookingPanel();
        clientRoomBookingPanel.centerDialog(dialog);
        dialog.setVisible(true);
    }

    private class BookedRoomFrame extends JPanel {
        private final Booking booking;
        private JLabel photoLabel;
        private JButton prevButton;
        private JButton nextButton;
        private JButton cancelButton;
        private int currentPhotoIndex = 0;
        private List<String> photoPaths;
        private JLabel roomNumberLabel;
        private JLabel bookingDatesLabel;
        private JLabel roomPriceLabel;
        private JLabel roomDescriptionLabel;

        public BookedRoomFrame(Booking booking) {
            this.booking = booking;
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
            
            roomNumberLabel = new JLabel("Номер: " + booking.getRoom().getNumber(), SwingConstants.CENTER);
            bookingDatesLabel = new JLabel("Даты: " + 
                DateUtils.convertDateToUI(booking.getStartDate()) + " - " + 
                DateUtils.convertDateToUI(booking.getEndDate()), 
                SwingConstants.CENTER);
            roomPriceLabel = new JLabel("Цена: " + booking.getRoom().getPrice() + " руб.", SwingConstants.CENTER);
            roomDescriptionLabel = new JLabel("Описание: " + booking.getRoom().getDescription(), SwingConstants.CENTER);

            roomNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            bookingDatesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roomPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roomDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            prevButton = new JButton("<<");
            cancelButton = new JButton("Отменить бронирование");
            nextButton = new JButton(">>");
            
            buttonPanel.add(prevButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(nextButton);
            buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            infoPanel.add(roomNumberLabel);
            infoPanel.add(bookingDatesLabel);
            infoPanel.add(roomPriceLabel);
            infoPanel.add(roomDescriptionLabel);
            infoPanel.add(buttonPanel);
            
            add(infoPanel, BorderLayout.SOUTH);

            cancelButton.addActionListener(e -> handleCancelBooking());
        }

        private void handleCancelBooking() {
            try {
                apiClient.cancelBooking(booking.getId());
                showCenteredMessage("Бронирование успешно отменено", "Уведомление", JOptionPane.INFORMATION_MESSAGE);
                loadBookings(); // Reload the bookings after cancellation
            } catch (Exception ex) {
                showCenteredMessage("Ошибка при отмене бронирования: " + ex.getMessage(), 
                "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Same photo handling methods as in RoomFrame
        private void initializePhotos() {
            photoPaths = new ArrayList<>();
            String basePath = "server\\src\\main\\resources\\images\\";
            List<String> roomPhotos = booking.getRoom().getPhotos();
            if (roomPhotos != null && !roomPhotos.isEmpty()) {
                photoPaths.addAll(roomPhotos);
            } else {
                photoPaths.add(basePath + "room1_1.jpg");
                photoPaths.add(basePath + "room1_2.jpg");
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
                                photoLabel.setText("Номер " + booking.getRoom().getNumber());
                            }
                        } catch (Exception e) {
                            photoLabel.setIcon(null);
                            photoLabel.setText("Номер " + booking.getRoom().getNumber());
                        }
                    }
                };
                
                worker.execute();
            }
        }
    }
}