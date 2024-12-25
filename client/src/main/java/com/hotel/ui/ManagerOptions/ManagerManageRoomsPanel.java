package com.hotel.ui.ManagerOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import com.hotel.client.HotelApiClient;
import com.hotel.dto.RoomDTO;
import com.hotel.utils.DateUtils;
import com.hotel.dto.BookingDTO;

public class ManagerManageRoomsPanel extends JPanel {
    private JPanel roomGridPanel;
    private List<RoomFrame> roomFrames;
    private HotelApiClient apiClient;

    public ManagerManageRoomsPanel() {
        apiClient = new HotelApiClient();
        setLayout(new BorderLayout());

        // Панель сетки номеров
        roomGridPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(roomGridPanel);
        JPanel gridPanelContainer = new JPanel(new BorderLayout());
        gridPanelContainer.add(scrollPane, BorderLayout.CENTER);

        // Кнопка для добавления номера
        JButton addRoomButton = new JButton("Добавить номер");
        addRoomButton.addActionListener(e -> showAddRoomDialog());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addRoomButton);
        gridPanelContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(gridPanelContainer, BorderLayout.CENTER);

        loadRoomsFromDatabase();
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
            updateRoomGrid();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке номеров: " + e.getMessage());
        }
    }

    private void updateRoomGrid() {
        roomGridPanel.removeAll();
        for (RoomFrame roomFrame : roomFrames) {
            roomGridPanel.add(roomFrame);
        }
        roomGridPanel.revalidate();
        roomGridPanel.repaint();
    }

    private void showAddRoomDialog() {
        JDialog addRoomDialog = new JDialog();
        addRoomDialog.setTitle("Добавить номер");
        addRoomDialog.setModal(true);
        addRoomDialog.setLayout(new BorderLayout());

        ManagerAddRoomPanel addRoomPanel = new ManagerAddRoomPanel();
        addRoomPanel.setUpdateCallback(this::loadRoomsFromDatabase); // Добавляем callback для обновления таблицы
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
        editRoomPanel.setUpdateCallback(this::loadRoomsFromDatabase); // Добавляем callback для обновления таблицы
        editRoomDialog.add(editRoomPanel, BorderLayout.CENTER);

        editRoomDialog.pack();
        editRoomDialog.setLocationRelativeTo(this);
        editRoomDialog.setVisible(true);
    }

    private void showDeleteRoomDialog(RoomDTO room) {
        int response = JOptionPane.showConfirmDialog(this, "Вы действительно хотите удалить номер?", "Удалить номер", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            try {
                apiClient.deleteRoomByNumber(room.getNumber());
                JOptionPane.showMessageDialog(this, "Номер успешно удален");
                loadRoomsFromDatabase();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении номера: " + e.getMessage());
            }
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

        // Start date
        gbc.gridx = 0;
        gbc.gridy = 0;
        bookingForm.add(new JLabel("Дата начала (ДД.ММ.ГГГГ):"), gbc);

        gbc.gridx = 1;
        JTextField startDateField = new JTextField(15);
        startDateField.setText(DateUtils.convertDateToUI(booking.getStartDate()));
        bookingForm.add(startDateField, gbc);

        // End date
        gbc.gridx = 0;
        gbc.gridy = 1;
        bookingForm.add(new JLabel("Дата конца (ДД.ММ.ГГГГ):"), gbc);

        gbc.gridx = 1;
        JTextField endDateField = new JTextField(15);
        endDateField.setText(DateUtils.convertDateToUI(booking.getEndDate()));
        bookingForm.add(endDateField, gbc);

        // Confirm button
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
            // Increase dimensions
            photoLabel.setPreferredSize(new Dimension(400, 300));
            photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            photoLabel.setBorder(BorderFactory.createEtchedBorder());
            
            // Set minimum size
            photoLabel.setMinimumSize(new Dimension(300, 225));

            JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton prevButton = new JButton("<");
            JButton nextButton = new JButton(">");

            prevButton.addActionListener(e -> {
                currentPhotoIndex = (currentPhotoIndex - 1 + room.getPhotos().size()) % room.getPhotos().size();
                updatePhoto();
            });

            nextButton.addActionListener(e -> {
                currentPhotoIndex = (currentPhotoIndex + 1) % room.getPhotos().size();
                updatePhoto();
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
        
            // Replace multiple booking buttons with a single manage booking button
            JButton manageBookingButton = new JButton("Управление бронированиями");
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
                // Implement logic to select and edit a booking
                try {
                    List<BookingDTO> bookings = apiClient.getAllBookingsByRoomNumber(room.getNumber());
                    if (bookings == null || bookings.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Нет бронирований для редактирования.");
                        return;
                    }
                    BookingDTO booking = (BookingDTO) JOptionPane.showInputDialog(
                        this,
                        "Выберите бронирование для редактирования:",
                        "Редактировать бронирование",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        bookings.toArray(),
                        bookings.get(0)
                    );
                    if (booking != null) {
                        showEditBookingDialog(booking);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при получении бронирований: " + ex.getMessage());
                }
            });

            deleteBookingItem.addActionListener(e -> {
                // Implement logic to select and delete a booking
                try {
                    List<BookingDTO> bookings = apiClient.getAllBookingsByRoomNumber(room.getNumber());
                    if (bookings == null || bookings.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Нет бронирований для удаления.");
                        return;
                    }
                    BookingDTO booking = (BookingDTO) JOptionPane.showInputDialog(
                        this,
                        "Выберите бронирование для удаления:",
                        "Удалить бронирование",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        bookings.toArray(),
                        bookings.get(0)
                    );
                    if (booking != null) {
                        int confirm = JOptionPane.showConfirmDialog(this,
                            "Вы действительно хотите удалить бронирование?",
                            "Подтверждение удаления",
                            JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                apiClient.deleteBooking(booking.getId());
                                JOptionPane.showMessageDialog(this, "Бронирование успешно удалено.");
                                loadRoomsFromDatabase();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(this, "Ошибка при удалении бронирования: " + ex.getMessage());
                            }
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при получении бронирований: " + ex.getMessage());
                }
            });

            popupMenu.add(editBookingItem);
            popupMenu.add(deleteBookingItem);
            popupMenu.show(this, 0, 0);
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
                            
                            // Calculate scaling ratio while maintaining aspect ratio
                            double widthRatio = 400.0 / originalImg.getWidth();
                            double heightRatio = 300.0 / originalImg.getHeight();
                            double ratio = Math.min(widthRatio, heightRatio);
                            
                            int newWidth = (int) (originalImg.getWidth() * ratio);
                            int newHeight = (int) (originalImg.getHeight() * ratio);
                            
                            // Use better quality scaling
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