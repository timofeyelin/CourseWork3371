package com.hotel.dto;

import java.time.LocalDate;

import com.hotel.model.Booking;

public class BookingDTO {
    private Long id;
    private Long userId;
    private String roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    public BookingDTO(Booking booking) {
        if (booking != null) {
            this.id = booking.getId();
            if (booking.getUser() != null) {
                this.userId = booking.getUser().getId();
            }
            if (booking.getRoom() != null) {
                this.roomNumber = booking.getRoom().getNumber();
            }
            this.startDate = booking.getStartDate();
            this.endDate = booking.getEndDate();
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Бронирование #" + id + " | Комната: " + roomNumber +
               " | Даты: " + startDate + " - " + endDate;
    }
}