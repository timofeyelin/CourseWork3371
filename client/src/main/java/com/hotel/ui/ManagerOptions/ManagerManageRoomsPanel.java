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
            e.printStackTrace();
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

    private void showDeleteBookingDialog(BookingDTO booking) {
        int response = JOptionPane.showConfirmDialog(this, "Вы действительно хотите отменить бронирование?", "Отменить бронирование", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            try {
                apiClient.deleteBooking(booking.getId());
                JOptionPane.showMessageDialog(this, "Бронирование успешно отменено");
                loadRoomsFromDatabase();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка при отмене бронирования: " + e.getMessage());
            }
        }
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
            photoLabel.setPreferredSize(new Dimension(200, 150));
            photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            photoLabel.setBorder(BorderFactory.createEtchedBorder());

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
        
            // Check if the room is booked
            try {
                final BookingDTO booking = apiClient.getBookingByRoomNumber(room.getNumber());
                if (booking != null) {
                    JButton editBookingButton = new JButton("Редактировать бронирование");
                    JButton deleteBookingButton = new JButton("Удалить бронирование");
        
                    editBookingButton.addActionListener(e -> showEditBookingDialog(booking));
                    deleteBookingButton.addActionListener(e -> showDeleteBookingDialog(booking));
        
                    buttonPanel.add(editBookingButton);
                    buttonPanel.add(deleteBookingButton);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        
            buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
            infoPanel.add(roomNumberLabel);
            infoPanel.add(roomTypeLabel);
            infoPanel.add(roomPriceLabel);
            infoPanel.add(roomDescriptionLabel);
            infoPanel.add(buttonPanel);
        
            add(infoPanel, BorderLayout.SOUTH);
        }

        private void updatePhoto() {
            if (!room.getPhotos().isEmpty()) {
                String photoPath = room.getPhotos().get(currentPhotoIndex);
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
}