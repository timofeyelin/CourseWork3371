package com.hotel.controller;

import com.hotel.controller.dto.BookingDTO;
import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.repository.BookingRepository;
import com.hotel.service.BookingService;
import com.hotel.service.RoomService;
import com.hotel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable Long userId) {
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/user/{userId}/details")
    public List<BookingDTO> getBookingsByUserId(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getUserBookings(userId);
        return bookings.stream().map(BookingDTO::new).toList();
    }


    @GetMapping("/room/{roomNumber}/all")
    public ResponseEntity<List<BookingDTO>> getAllBookingsByRoomNumber(@PathVariable String roomNumber) {
        try {
            Room room = roomService.getRoomByNumber(roomNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));
                    
            List<Booking> bookings = bookingRepository.findByRoomId(room.getId());
            List<BookingDTO> bookingDTOs = bookings.stream()
                    .map(BookingDTO::new)
                    .toList();
                    

            return ResponseEntity.ok(bookingDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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
                roomService.updateRoomByNumber(room.getNumber(), room);
            }
            
            // Cancel booking
            bookingService.cancelBooking(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkRoomAvailability(
        @RequestParam String roomNumber,
        @RequestParam String startDate,
        @RequestParam String endDate) {
        try {
            Room room = roomService.getRoomByNumber(roomNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));
            
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            // Получаем все бронирования для номера
            List<Booking> bookings = bookingRepository.findByRoomId(room.getId());
            
            // Проверяем пересечение с существующими бронированиями
            boolean isAvailable = bookings.stream().noneMatch(booking ->
                !(end.isBefore(booking.getStartDate()) || start.isAfter(booking.getEndDate()))
            );
            
            return ResponseEntity.ok(isAvailable);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}