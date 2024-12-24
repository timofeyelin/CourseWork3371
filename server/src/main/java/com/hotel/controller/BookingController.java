package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.service.BookingService;
import com.hotel.service.RoomService;
import com.hotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/room/{roomNumber}")
    public BookingDTO getBookingByRoomNumber(@PathVariable String roomNumber) {
        Booking booking = bookingService.getBookingByRoomNumber(roomNumber);
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирование не найдено");
        }
        return new BookingDTO(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id, @RequestBody Map<String, String> bookingData) {
        try {
            LocalDate startDate = LocalDate.parse(bookingData.get("startDate"));
            LocalDate endDate = LocalDate.parse(bookingData.get("endDate"));
            Booking updatedBooking = bookingService.updateBooking(id, startDate, endDate);
            return ResponseEntity.ok(new BookingDTO(updatedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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

        room.setAvailable(false);
        roomService.updateRoomByNumber(roomNumber, room);

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);

        return bookingService.createBooking(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            // Get booking before deletion to access room info
            Booking booking = bookingService.getBookingById(id);
            if (booking != null) {
                // Get room and update availability
                Room room = booking.getRoom();
                room.setAvailable(true);
                roomService.updateRoomByNumber(room.getNumber(), room);
            }
            
            // Cancel booking
            bookingService.cancelBooking(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}