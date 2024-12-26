package com.hotel.ui.ManagerOptions;

import com.hotel.client.HotelApiClient;
import com.hotel.dto.BookingDTO;
import com.hotel.dto.UserDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerClientInfoPanel extends JPanel {
    private HotelApiClient apiClient;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<UserDTO> allUsers;

    private static final DateTimeFormatter SERVER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter UI_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ManagerClientInfoPanel() {
        apiClient = new HotelApiClient();
        setLayout(new BorderLayout());

        // Создаем поле для ввода логина
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterUsers();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterUsers();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterUsers();
            }
        });

        // Создаем модель таблицы с двумя столбцами
        tableModel = new NonEditableTableModel(new Object[]{"Логин", "Бронирования"}, 0);
        clientTable = new JTable(tableModel);
        clientTable.setPreferredScrollableViewportSize(new Dimension(500, 300));
        clientTable.setFillsViewportHeight(true);

        // Устанавливаем кастомный рендерер для всех столбцов
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        clientTable.setDefaultRenderer(Object.class, renderer);

        // Добавляем таблицу в JScrollPane для прокрутки
        JScrollPane scrollPane = new JScrollPane(clientTable);

        // Добавляем компоненты на панель
        add(searchField, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Загружаем данные в таблицу
        loadClientData();
    }

    private void loadClientData() {
        try {
            allUsers = apiClient.getAllUsers();
            filterUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        List<UserDTO> filteredUsers = allUsers.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(searchText))
                .filter(user -> !"ADMIN".equals(user.getRole()) && !"MANAGER".equals(user.getRole()))
                .collect(Collectors.toList());

        updateTable(filteredUsers);
    }

    private void updateTable(List<UserDTO> users) {
        tableModel.setRowCount(0); // Очистить таблицу
        for (UserDTO user : users) {
            try {
                List<BookingDTO> bookings = apiClient.getBookingsByUserId(user.getId());
                String bookingsInfo = bookings.isEmpty() ? "Нет бронирований" : formatBookings(bookings);
                tableModel.addRow(new Object[]{user.getUsername(), bookingsInfo});
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка при загрузке бронирований для пользователя: " + user.getUsername(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String formatBookings(List<BookingDTO> bookings) {
        StringBuilder sb = new StringBuilder("<html>");
        for (BookingDTO booking : bookings) {
            LocalDate startDate = LocalDate.parse(booking.getStartDate().toString(), SERVER_DATE_FORMATTER);
            LocalDate endDate = LocalDate.parse(booking.getEndDate().toString(), SERVER_DATE_FORMATTER);
            sb.append(String.format("Номер: %s, с %s по %s<br>",
                    booking.getRoomNumber(),
                    startDate.format(UI_DATE_FORMATTER),
                    endDate.format(UI_DATE_FORMATTER)));
        }
        sb.append("</html>");
        return sb.toString();
    }

    // Класс для создания не редактируемой модели таблицы
    private static class NonEditableTableModel extends DefaultTableModel {
        public NonEditableTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Все ячейки не редактируемые
        }
    }

    // Класс для рендеринга ячеек с многострочным текстом
    private static class CustomTableCellRenderer extends JLabel implements TableCellRenderer {
        public CustomTableCellRenderer() {
            setVerticalAlignment(TOP);
            setHorizontalAlignment(LEFT);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            setFont(table.getFont());
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            if (table.getRowHeight(row) != getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
    }

    public void refreshTableData() {
        loadClientData();
    }
}