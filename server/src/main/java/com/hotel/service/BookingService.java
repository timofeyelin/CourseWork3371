package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public Booking createBooking(Booking booking) {
        LocalDate today = LocalDate.now();

        if (booking.getStartDate().isBefore(today) || booking.getEndDate().isBefore(today)) {
            throw new IllegalArgumentException("Нельзя бронировать на прошедшие даты");
        }

        if (booking.getEndDate().isBefore(booking.getStartDate())) {
            throw new IllegalArgumentException("Дата окончания бронирования не может быть раньше даты начала");
        }

        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                booking.getRoom().getId(), booking.getStartDate(), booking.getEndDate());

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalArgumentException("Номер уже забронирован на выбранные даты");
        }

        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));
    }
}