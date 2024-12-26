package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.imageio.ImageIO;
import com.hotel.client.HotelApiClient;
import com.hotel.dto.RoomDTO;
import com.hotel.utils.DateLabelFormatter;
import com.hotel.utils.DateUtils;
import com.hotel.dto.BookingDTO;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class ManagerManageRoomsPanel extends JPanel {
    private JPanel roomGridPanel;
    private List<RoomFrame> roomFrames;
    private HotelApiClient apiClient;
    private JComboBox<String> roomTypeFilterComboBox;
    private JTextField minPriceFilterField;
    private JTextField maxPriceFilterField;
    private JDatePickerImpl filterStartDatePicker;
    private JDatePickerImpl filterEndDatePicker;

    private JButton prevPageButton;
    private JButton nextPageButton;
    private JLabel pageNumberLabel;
    private int currentPage = 0;
    private int roomsPerPage = 2;

    public ManagerManageRoomsPanel() {
        apiClient = new HotelApiClient();
        setLayout(new BorderLayout());

        roomGridPanel = new JPanel(new GridLayout(1, roomsPerPage, 10, 10));
        JScrollPane scrollPane = new JScrollPane(roomGridPanel);
        JPanel gridPanelContainer = new JPanel(new BorderLayout());
        gridPanelContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new GridLayout(2, 5, 10, 10));

        JLabel typeLabel = new JLabel("Тип номера:");
        roomTypeFilterComboBox = new JComboBox<>(new String[]{"Все", "Одноместный", "Двухместный", "Люкс"});
        filterPanel.add(typeLabel);
        filterPanel.add(roomTypeFilterComboBox);

        JLabel minPriceLabel = new JLabel("Мин. цена:");
        minPriceFilterField = new JTextField();
        filterPanel.add(minPriceLabel);
        filterPanel.add(minPriceFilterField);

        JLabel maxPriceLabel = new JLabel("Макс. цена:");
        maxPriceFilterField = new JTextField();
        filterPanel.add(maxPriceLabel);
        filterPanel.add(maxPriceFilterField);

        JLabel startDateLabel = new JLabel("Дата начала:");
        filterStartDatePicker = createDatePicker();
        filterPanel.add(startDateLabel);
        filterPanel.add(filterStartDatePicker);

        JLabel endDateLabel = new JLabel("Дата конца:");
        filterEndDatePicker = createDatePicker();
        filterPanel.add(endDateLabel);
        filterPanel.add(filterEndDatePicker);

        JButton filterButton = new JButton("Поиск");
        filterButton.addActionListener(e -> {
            String selectedType = (String) roomTypeFilterComboBox.getSelectedItem();
            String minPriceText = minPriceFilterField.getText().trim();
            String maxPriceText = maxPriceFilterField.getText().trim();
            LocalDate startDate = getSelectedDate(filterStartDatePicker);
            LocalDate endDate = getSelectedDate(filterEndDatePicker);

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
        filterPanel.add(new JLabel());
        filterPanel.add(filterButton);

        gridPanelContainer.add(filterPanel, BorderLayout.NORTH);

        JButton addRoomButton = new JButton("Добавить номер");
        addRoomButton.addActionListener(e -> showAddRoomDialog());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addRoomButton);
        gridPanelContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(gridPanelContainer, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout());
        prevPageButton = new JButton("Предыдущая");
        nextPageButton = new JButton("Следующая");
        pageNumberLabel = new JLabel("Страница 1 из 1");

        prevPageButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateRoomGrid();
            }
        });

        nextPageButton.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) roomFrames.size() / roomsPerPage);
            if (currentPage < totalPages - 1) {
                currentPage++;
                updateRoomGrid();
            }
        });

        paginationPanel.add(prevPageButton);
        paginationPanel.add(pageNumberLabel);
        paginationPanel.add(nextPageButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(paginationPanel, BorderLayout.SOUTH);
        gridPanelContainer.add(bottomPanel, BorderLayout.SOUTH);

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

    private void loadRoomsFromDatabase() {
        try {
            List<RoomDTO> rooms = apiClient.getAllRooms();
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
        if (totalPages == 0) totalPages = 1;

        pageNumberLabel.setText("Страница " + (currentPage + 1) + " из " + totalPages);

        prevPageButton.setEnabled(currentPage > 0);
        nextPageButton.setEnabled(currentPage < totalPages - 1);
    }

    private void showAddRoomDialog() {
        JDialog addRoomDialog = new JDialog();
        addRoomDialog.setTitle("Добавить номер");
        addRoomDialog.setModal(true);
        addRoomDialog.setLayout(new BorderLayout());

        ManagerAddRoomPanel addRoomPanel = new ManagerAddRoomPanel();
        addRoomPanel.setUpdateCallback(this::loadRoomsFromDatabase);
        addRoomDialog.add(addRoomPanel, BorderLayout.CENTER);

        addRoomDialog.pack();
        addRoomDialog.setLocationRelativeTo(this);
        addRoomDialog.setVisible(true);
    }

    private void showEditRoomDialog(RoomDTO room) {
        JDialog editRoomDialog = new JDialog();
        editRoomDialog.setTitle("Редактировать номер");
        editRoomDialog.setModal(true);
        editRoomDialog.setLayout(new BorderLayout());

        ManagerEditRoomPanel editRoomPanel = new ManagerEditRoomPanel(room);
        editRoomPanel.setUpdateCallback(this::loadRoomsFromDatabase);
        editRoomDialog.add(editRoomPanel, BorderLayout.CENTER);

        editRoomDialog.pack();
        editRoomDialog.setLocationRelativeTo(this);
        editRoomDialog.setVisible(true);
    }

    private void showDeleteRoomDialog(RoomDTO room) {
        try {
            // Проверяем наличие бронирований
            List<BookingDTO> bookings = apiClient.getAllBookingsByRoomNumber(room.getNumber());
            
            String message = "Вы действительно хотите удалить номер?";
            if (bookings != null && !bookings.isEmpty()) {
                message = "Внимание! У этого номера есть активные бронирования.\n" +
                         "При удалении номера все бронирования будут отменены.\n" +
                         "Вы действительно хотите удалить номер?";
            }
    
            Object[] options = {"Да", "Нет"};
            int response = JOptionPane.showOptionDialog(
                this,
                message,
                "Удалить номер",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]
            );
    
            if (response == JOptionPane.YES_OPTION) {
                apiClient.deleteRoomByNumber(room.getNumber());
                JOptionPane.showMessageDialog(this, "Номер успешно удален");
                loadRoomsFromDatabase();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Ошибка при удалении номера: " + e.getMessage(),
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showEditBookingDialog(BookingDTO booking) {
        JDialog editBookingDialog = new JDialog();
        editBookingDialog.setTitle("Редактировать бронирование");
        editBookingDialog.setModal(true);
        editBookingDialog.setLayout(new BorderLayout());

        JPanel bookingForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        bookingForm.add(new JLabel("Дата начала (ДД.ММ.ГГГГ):"), gbc);

        gbc.gridx = 1;
        JTextField startDateField = new JTextField(15);
        startDateField.setText(DateUtils.convertDateToUI(booking.getStartDate()));
        bookingForm.add(startDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        bookingForm.add(new JLabel("Дата конца (ДД.ММ.ГГГГ):"), gbc);

        gbc.gridx = 1;
        JTextField endDateField = new JTextField(15);
        endDateField.setText(DateUtils.convertDateToUI(booking.getEndDate()));
        bookingForm.add(endDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton confirmButton = new JButton("Подтвердить изменения");
        bookingForm.add(confirmButton, gbc);

        confirmButton.addActionListener(e -> {
            try {
                String startDate = startDateField.getText();
                String endDate = endDateField.getText();

                if (!DateUtils.isValidDate(startDate) || !DateUtils.isValidDate(endDate)) {
                    throw new IllegalArgumentException("Неверный формат даты. Ожидается ДД.ММ.ГГГГ");
                }

                String startDateFormatted = DateUtils.convertDateToServer(startDate);
                String endDateFormatted = DateUtils.convertDateToServer(endDate);

                apiClient.updateBooking(booking.getId(), startDateFormatted, endDateFormatted);
                JOptionPane.showMessageDialog(editBookingDialog, "Бронирование успешно обновлено");
                editBookingDialog.dispose();
                loadRoomsFromDatabase();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editBookingDialog, "Ошибка при обновлении бронирования: " + ex.getMessage());
            }
        });

        editBookingDialog.add(bookingForm, BorderLayout.CENTER);
        editBookingDialog.pack();
        editBookingDialog.setLocationRelativeTo(this);
        editBookingDialog.setVisible(true);
    }

    private class RoomFrame extends JPanel {
        private final RoomDTO room;
        private JLabel photoLabel;
        private int currentPhotoIndex = 0;
        private JButton manageBookingButton;

        public RoomFrame(RoomDTO room) {
            this.room = room;
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createEtchedBorder());

            setupPhotoPanel();
            setupInfoPanel();
        }

        private void setupPhotoPanel() {
            JPanel photoPanel = new JPanel(new BorderLayout());
            photoLabel = new JLabel();
            photoLabel.setPreferredSize(new Dimension(400, 300));
            photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            photoLabel.setBorder(BorderFactory.createEtchedBorder());
            
            photoLabel.setMinimumSize(new Dimension(300, 225));

            JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton prevButton = new JButton("<");
            JButton nextButton = new JButton(">");

            prevButton.addActionListener(e -> {
                if (!room.getPhotos().isEmpty()) {
                    currentPhotoIndex = (currentPhotoIndex - 1 + room.getPhotos().size()) % room.getPhotos().size();
                    updatePhoto();
                }
            });

            nextButton.addActionListener(e -> {
                if (!room.getPhotos().isEmpty()) {
                    currentPhotoIndex = (currentPhotoIndex + 1) % room.getPhotos().size();
                    updatePhoto();
                }
            });

            navPanel.add(prevButton);
            navPanel.add(nextButton);

            photoPanel.add(photoLabel, BorderLayout.CENTER);
            photoPanel.add(navPanel, BorderLayout.SOUTH);

            add(photoPanel, BorderLayout.CENTER);

            updatePhoto();
        }

        private void setupInfoPanel() {
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
            JLabel roomNumberLabel = new JLabel("Номер: " + room.getNumber(), SwingConstants.CENTER);
            JLabel roomTypeLabel = new JLabel("Тип: " + room.getType(), SwingConstants.CENTER);
            JLabel roomPriceLabel = new JLabel("Цена: " + room.getPrice() + " руб.", SwingConstants.CENTER);
            JLabel roomDescriptionLabel = new JLabel("Описание: " + room.getDescription(), SwingConstants.CENTER);
        
            roomNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roomTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roomPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            roomDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton editButton = new JButton("Редактировать");
            JButton deleteButton = new JButton("Удалить");
        
            editButton.addActionListener(e -> showEditRoomDialog(room));
            deleteButton.addActionListener(e -> showDeleteRoomDialog(room));
        
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
        
            manageBookingButton = new JButton("Управление бронированиями");
            manageBookingButton.addActionListener(e -> showManageBookingOptions());

            buttonPanel.add(manageBookingButton);
        
            buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
            infoPanel.add(roomNumberLabel);
            infoPanel.add(roomTypeLabel);
            infoPanel.add(roomPriceLabel);
            infoPanel.add(roomDescriptionLabel);
            infoPanel.add(buttonPanel);
        
            add(infoPanel, BorderLayout.SOUTH);
        }

        private void showManageBookingOptions() {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem editBookingItem = new JMenuItem("Редактировать бронирование");
            JMenuItem deleteBookingItem = new JMenuItem("Удалить бронирование");

            editBookingItem.addActionListener(e -> {
                try {
                    List<BookingDTO> bookings = apiClient.getAllBookingsByRoomNumber(room.getNumber());
                    if (bookings == null || bookings.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Нет бронирований для редактирования.");
                        return;
                    }
                    BookingDisplay[] bookingArray = bookings.stream()
                        .map(BookingDisplay::new)
                        .toArray(BookingDisplay[]::new);

                    BookingDisplay selectedDisplay = (BookingDisplay) JOptionPane.showInputDialog(
                        this,
                        "Выберите бронирование для редактирования:",
                        "Редактировать бронирование",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        bookingArray,
                        bookingArray[0]
                    );
                    if (selectedDisplay != null) {
                        showEditBookingDialog(selectedDisplay.getBooking());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при получении бронирований: " + ex.getMessage());
                }
            });

            deleteBookingItem.addActionListener(e -> {
                try {
                    List<BookingDTO> bookings = apiClient.getAllBookingsByRoomNumber(room.getNumber());
                    if (bookings == null || bookings.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Нет бронирований для удаления.");
                        return;
                    }
                    BookingDisplay[] bookingArray = bookings.stream()
                        .map(BookingDisplay::new)
                        .toArray(BookingDisplay[]::new);

                    BookingDisplay selectedDisplay = (BookingDisplay) JOptionPane.showInputDialog(
                        this,
                        "Выберите бронирование для удаления:",
                        "Удалить бронирование",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        bookingArray,
                        bookingArray[0]
                    );
                    if (selectedDisplay != null) {
                        int confirm = JOptionPane.showConfirmDialog(this,
                            "Вы действительно хотите удалить бронирование?",
                            "Подтверждение удаления",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                            apiClient.deleteBooking(selectedDisplay.getBooking().getId());
                            JOptionPane.showMessageDialog(this, "Бронирование успешно удалено.");
                            loadRoomsFromDatabase();
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при получении бронирований: " + ex.getMessage());
                }
            });

            popupMenu.add(editBookingItem);
            popupMenu.add(deleteBookingItem);
            popupMenu.show(manageBookingButton, manageBookingButton.getWidth(), 0);
        }

        private class BookingDisplay {
            private final BookingDTO booking;

            public BookingDisplay(BookingDTO booking) {
                this.booking = booking;
            }

            @Override
            public String toString() {
                return "С " + DateUtils.convertDateToUI(booking.getStartDate())
                    + " по " + DateUtils.convertDateToUI(booking.getEndDate());
            }

            public BookingDTO getBooking() {
                return booking;
            }
        }

        private void updatePhoto() {
            if (!room.getPhotos().isEmpty()) {
                photoLabel.setIcon(null);
                photoLabel.setText("Загрузка...");
                
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() {
                        try {
                            File imageFile = new File(room.getPhotos().get(currentPhotoIndex));
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
                                photoLabel.setText("Номер " + room.getNumber());
                            }
                        } catch (Exception e) {
                            photoLabel.setIcon(null);
                            photoLabel.setText("Номер " + room.getNumber());
                        }
                    }
                };
                
                worker.execute();
            } else {
                photoLabel.setIcon(null);
                photoLabel.setText("Нет изображений");
            }
        }
    }
}