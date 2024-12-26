package com.hotel.ui.ClientOptions;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.hotel.client.HotelApiClient;
import com.hotel.dto.BookingDTO;
import com.hotel.dto.RoomDTO;
import com.hotel.utils.DateLabelFormatter;
import com.hotel.utils.DateUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.util.Properties;

public class ClientRoomBookingPanel extends JPanel {
    private JPanel roomGridPanel;
    private JButton prevPageButton;
    private JButton nextPageButton;
    private int currentPage = 0;
    private int roomsPerPage = 2;
    private List<RoomFrame> roomFrames;
    private HotelApiClient apiClient;
    private JLabel pageNumberLabel;
    private JComboBox<String> roomTypeComboBox;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JDatePickerImpl startDatePicker;
    private JDatePickerImpl endDatePicker;

    public ClientRoomBookingPanel() {
        apiClient = new HotelApiClient();
        setLayout(new BorderLayout());

        // Панель поиска
    JPanel searchPanel = new JPanel(new GridLayout(2, 5, 10, 10));

    // Тип номера
    JLabel typeLabel = new JLabel("Тип номера:");
    roomTypeComboBox = new JComboBox<>(new String[]{"Все", "Одноместный", "Двухместный", "Люкс"});
    searchPanel.add(typeLabel);
    searchPanel.add(roomTypeComboBox);

    // Цена от
    JLabel minPriceLabel = new JLabel("Мин. цена:");
    minPriceField = new JTextField();
    searchPanel.add(minPriceLabel);
    searchPanel.add(minPriceField);

    // Цена до
    JLabel maxPriceLabel = new JLabel("Макс. цена:");
    maxPriceField = new JTextField();
    searchPanel.add(maxPriceLabel);
    searchPanel.add(maxPriceField);

    // Дата начала
    JLabel startDateLabel = new JLabel("Дата начала:");
    startDatePicker = createDatePicker();
    searchPanel.add(startDateLabel);
    searchPanel.add(startDatePicker);

    // Дата конца
    JLabel endDateLabel = new JLabel("Дата конца:");
    endDatePicker = createDatePicker();
    searchPanel.add(endDateLabel);
    searchPanel.add(endDatePicker);

    // Кнопка поиска
    JButton searchButton = new JButton("Поиск");
    searchButton.addActionListener(e -> {
        String selectedType = (String) roomTypeComboBox.getSelectedItem();
        String minPriceText = minPriceField.getText();
        String maxPriceText = maxPriceField.getText();
        LocalDate startDate = getSelectedDate(startDatePicker);
        LocalDate endDate = getSelectedDate(endDatePicker);

        BigDecimal minPrice = minPriceText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(minPriceText);
        BigDecimal maxPrice = maxPriceText.isEmpty() ? BigDecimal.valueOf(Double.MAX_VALUE) : new BigDecimal(maxPriceText);

        loadRoomsFromDatabase(selectedType, minPrice, maxPrice, startDate, endDate);
    });
    searchPanel.add(new JLabel()); // Empty cell
    searchPanel.add(searchButton);
    
    add(searchPanel, BorderLayout.NORTH);

        // Панель сетки номеров
        roomGridPanel = new JPanel(new GridLayout(1, 2, 10, 10));
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
        pageNumberLabel = new JLabel("Страница 1 из 1");
        paginationPanel.add(prevPageButton);
        paginationPanel.add(pageNumberLabel);
        paginationPanel.add(nextPageButton);
        gridPanelContainer.add(paginationPanel, BorderLayout.SOUTH);

        add(gridPanelContainer, BorderLayout.CENTER);

        loadRoomsFromDatabase();

        searchButton.addActionListener(e -> {
            String selectedType = (String) roomTypeComboBox.getSelectedItem();
            String minPriceText = minPriceField.getText().trim();
            String maxPriceText = maxPriceField.getText().trim();
            LocalDate startDate = getSelectedDate(startDatePicker);
            LocalDate endDate = getSelectedDate(endDatePicker);
        
            BigDecimal minPrice = BigDecimal.ZERO;
            BigDecimal maxPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        
            try {
                if (!minPriceText.isEmpty()) {
                    minPrice = new BigDecimal(minPriceText);
                    if (minPrice.compareTo(BigDecimal.ZERO) < 0) {
                        throw new NumberFormatException("Мин. цена не может быть отрицательной.");
                    }
                }
        
                if (!maxPriceText.isEmpty()) {
                    maxPrice = new BigDecimal(maxPriceText);
                    if (maxPrice.compareTo(minPrice) < 0) {
                        throw new NumberFormatException("Макс. цена не может быть меньше мин. цены.");
                    }
                }
        
                if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                    throw new IllegalArgumentException("Дата конца не может быть раньше даты начала.");
                }
        
                loadRoomsFromDatabase(selectedType, minPrice, maxPrice, startDate, endDate);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Неправильный ввод цены: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadRoomsFromDatabase() {
        try {
            List<RoomDTO> rooms = apiClient.getAllRooms();
            roomFrames = new ArrayList<>();
            if (rooms != null) {
                for (RoomDTO room : rooms) {
                    roomFrames.add(new RoomFrame(room));
                }
            }
            currentPage = 0; // Reset to first page
            updateRoomGrid();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке номеров: " + e.getMessage());
        }
    }

    private void loadRoomsFromDatabase(String roomType, BigDecimal minPrice, BigDecimal maxPrice, LocalDate startDate, LocalDate endDate) {
        try {
            List<RoomDTO> rooms = apiClient.getFilteredRooms(roomType, minPrice, maxPrice, startDate, endDate);
            roomFrames = new ArrayList<>();
            if (rooms != null) {
                for (RoomDTO room : rooms) {
                    roomFrames.add(new RoomFrame(room));
                }
            }
            currentPage = 0;
            updateRoomGrid();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке номеров: " + e.getMessage());
        }
    }

    public void refreshRoomGrid() {
        loadRoomsFromDatabase();
    }

    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Сегодня");
        p.put("text.month", "Месяц");
        p.put("text.year", "Год");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private LocalDate getSelectedDate(JDatePickerImpl datePicker) {
        if (datePicker.getModel().getValue() != null) {
            java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
            return selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
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

        int totalPages = (int) Math.ceil((double) roomFrames.size() / roomsPerPage);
        pageNumberLabel.setText("Страница " + (currentPage + 1) + " из " + totalPages);

        prevPageButton.setEnabled(currentPage > 0);
        nextPageButton.setEnabled(currentPage < totalPages - 1);
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
        private JLabel roomDescriptionLabel;
        private JButton bookingInfoLabel; // New label

        public RoomFrame(RoomDTO room) {
            this.room = room;
            this.apiClient = ClientRoomBookingPanel.this.apiClient;
            this.roomNumber = room.getNumber();
            this.photoPaths = room.getPhotos() != null ? room.getPhotos() : new ArrayList<>();
            
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createEtchedBorder());

            setupPhotoPanel();
            setupInfoPanel();
            addPhotoNavigationListeners();
            updateBookingInfoLabel();
        }

        private void setupPhotoPanel() {
            JPanel photoPanel = new JPanel(new BorderLayout());
            photoLabel = new JLabel();
            photoLabel.setPreferredSize(new Dimension(400, 300));
            photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            photoLabel.setBorder(BorderFactory.createEtchedBorder());
            
            photoLabel.setMinimumSize(new Dimension(300, 225));
            
            photoPanel.add(photoLabel, BorderLayout.CENTER);
            add(photoPanel, BorderLayout.CENTER);
            
            updatePhoto();
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
            bookingInfoLabel = new JButton("Информация о бронированиях");
            bookingInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            bookingInfoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            bookingInfoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showBookingInfoDialog();
                }
            });
            infoPanel.add(bookingInfoLabel);
            

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
                        
                        // Проверяем доступность номера на выбранные даты
                        boolean isAvailable = apiClient.checkRoomAvailability(
                            room.getNumber(), startDate, endDate);
                            
                        if (!isAvailable) {
                            JOptionPane.showMessageDialog(bookingDialog, 
                                "Номер уже забронирован на выбранные даты",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        apiClient.createBooking(room.getNumber(), userId, startDate, endDate);
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
            centerDialog(bookingDialog);
            bookingDialog.setVisible(true);
        }

       

        private void addPhotoNavigationListeners() {
            prevButton.addActionListener(e -> {
                if (photoPaths != null && !photoPaths.isEmpty()) {
                    currentPhotoIndex = (currentPhotoIndex - 1 + photoPaths.size()) % photoPaths.size();
                    updatePhoto();
                }
            });

            nextButton.addActionListener(e -> {
                if (photoPaths != null && !photoPaths.isEmpty()) {
                    currentPhotoIndex = (currentPhotoIndex + 1) % photoPaths.size();
                    updatePhoto();
                }
            });
        }

        private void updatePhoto() {
            if (photoPaths != null && !photoPaths.isEmpty()) {
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
                            BufferedImage originalImg = ImageIO.read(imageFile);
                            if (originalImg == null) {
                                return null;
                            }
                            
                            double widthRatio = 400.0 / originalImg.getWidth();
                            double heightRatio = 300.0 / originalImg.getHeight();
                            double ratio = Math.min(widthRatio, heightRatio);
                            
                            int newWidth = (int) (originalImg.getWidth() * ratio);
                            int newHeight = (int) (originalImg.getHeight() * ratio);
                            
                            Image scaledImg = originalImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
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
            } else {
                photoLabel.setIcon(null);
                photoLabel.setText("Нет изображений");
            }
        }

        private void showBookingInfoDialog() {
            JDialog infoDialog = new JDialog();
            infoDialog.setTitle("Информация о бронированиях");
            infoDialog.setLayout(new BorderLayout(10, 10));
            infoDialog.setModal(true);
        
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
            try {
                List<BookingDTO> bookings = apiClient.getAllBookingsByRoomNumber(roomNumber);
        
                if (bookings != null && !bookings.isEmpty()) {
                    JLabel headerLabel = new JLabel("<html><b>Список бронирований:</b></html>");
                    headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    infoPanel.add(headerLabel);
                    infoPanel.add(Box.createVerticalStrut(10));
        
                    // Sort bookings by start date
                    bookings.sort((b1, b2) -> b1.getStartDate().compareTo(b2.getStartDate()));
        
                    LocalDate now = LocalDate.now();
                    boolean hasCurrentBookings = false;
        
                    for (BookingDTO booking : bookings) {
                        if (booking.getEndDate().isBefore(now)) {
                            continue; // Skip past bookings
                        }
        
                        hasCurrentBookings = true;
        
                        JLabel bookingLabel = new JLabel(String.format(
                            "<html>&#8226; Период: с %s по %s<br>" +
                            "&nbsp;&nbsp;&nbsp;Доступен с: %s</html>",
                            DateUtils.convertDateToUI(booking.getStartDate()),
                            DateUtils.convertDateToUI(booking.getEndDate()),
                            DateUtils.convertDateToUI(booking.getEndDate().plusDays(1))
                        ));
                        bookingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        infoPanel.add(bookingLabel);
                        infoPanel.add(Box.createVerticalStrut(10));
                    }
        
                    if (!hasCurrentBookings) {
                        JLabel availableLabel = new JLabel(
                            "<html><b>Номер свободен для бронирования</b><br>" +
                            "Вы можете забронировать этот номер прямо сейчас</html>"
                        );
                        availableLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        infoPanel.add(availableLabel);
                    }
                } else {
                    JLabel availableLabel = new JLabel(
                        "<html><b>Номер свободен для бронирования</b><br>" +
                        "Вы можете забронировать этот номер прямо сейчас</html>"
                    );
                    availableLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    infoPanel.add(availableLabel);
                }
            } catch (Exception e) {
                JLabel errorLabel = new JLabel("Ошибка при загрузке информации");
                errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                infoPanel.add(errorLabel);
            }
        
            JScrollPane scrollPane = new JScrollPane(infoPanel);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            infoDialog.add(scrollPane, BorderLayout.CENTER);
        
            infoDialog.pack();
            centerDialog(infoDialog);
            infoDialog.setVisible(true);
        }

        private void updateBookingInfoLabel() {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    try {
                        List<BookingDTO> bookings = apiClient.getAllBookingsByRoomNumber(roomNumber);
                        return bookings != null && !bookings.isEmpty();
                    } catch (Exception e) {
                        return false;
                    }
                }
    
                @Override
                protected void done() {
                    try {
                        boolean hasBookings = get();
                        if (hasBookings) {
                            bookingInfoLabel.setText("Информация о бронированиях (Есть бронирования)");
                        } else {
                            bookingInfoLabel.setText("Информация о бронированиях (Нет бронирований)");
                        }
                    } catch (Exception e) {
                        bookingInfoLabel.setText("Информация о бронированиях (Нет бронирований)");
                    }
                }
            };
            worker.execute();
        }
    }
}