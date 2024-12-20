package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.service.BookingService;
import com.hotel.service.RoomService;
import com.hotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable Long userId) {
        return bookingService.getUserBookings(userId);
    }

    @PostMapping
    public Booking createBooking(@RequestBody Map<String, String> bookingData) {
        String roomNumber = bookingData.get("roomNumber");
        Long userId = Long.parseLong(bookingData.get("userId"));
        LocalDate startDate = LocalDate.parse(bookingData.get("startDate"));
        LocalDate endDate = LocalDate.parse(bookingData.get("endDate"));

        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Номер комнаты не предоставлен");
        }

        Room room = roomService.getRoomByNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);

        return bookingService.createBooking(booking);
    }
}