package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomByNumber(String number) {
        return roomRepository.findByNumber(number);
    }

    public List<Room> getAvailableRooms(String type) {
        List<Room> rooms;
        LocalDate today = LocalDate.now();

        if (type != null && !type.isEmpty()) {
            rooms = roomRepository.findByType(type);
        } else {
            rooms = roomRepository.findAll();
        }

        return rooms.stream()
                .filter(room -> {
                    List<Booking> bookings = bookingRepository.findByRoomId(room.getId());

                    if (bookings.isEmpty()) {
                        return true;
                    }

                    return bookings.stream().noneMatch(booking ->
                        (today.isAfter(booking.getStartDate()) && today.isBefore(booking.getEndDate())) ||
                        today.isEqual(booking.getStartDate()) ||
                        today.isEqual(booking.getEndDate())
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Room createRoom(Room room) {
        if (room.getNumber() == null || room.getNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        return roomRepository.save(room);
    }

    public List<Room> searchRooms(String type, Double minPrice, Double maxPrice, LocalDate startDate, LocalDate endDate) {
        List<Room> rooms = roomRepository.findAll();

        return rooms.stream()
                .filter(room -> {
                    boolean matches = true;
                    if (type != null && !type.equalsIgnoreCase("Все")) {
                        matches &= room.getType().equalsIgnoreCase(type);
                    }
                    if (minPrice != null) {
                        matches &= room.getPrice().doubleValue() >= minPrice;
                    }
                    if (maxPrice != null) {
                        matches &= room.getPrice().doubleValue() <= maxPrice;
                    }
                    if (startDate != null && endDate != null) {
                        List<Booking> bookings = bookingRepository.findByRoomId(room.getId());
                        for (Booking booking : bookings) {
                            if (!(endDate.isBefore(booking.getStartDate()) || startDate.isAfter(booking.getEndDate()))) {
                                return false;
                            }
                        }
                    }
                    return matches;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Room updateRoomByNumber(String roomNumber, Room roomDetails) {
        Room room = roomRepository.findByNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));

        room.setType(roomDetails.getType());
        room.setPrice(roomDetails.getPrice());
        if (roomDetails.getDescription() != null) {
            room.setDescription(roomDetails.getDescription());
        }
        if (roomDetails.getPhotos() != null) {
            room.setPhotos(roomDetails.getPhotos());
        } else {
            room.setPhotos(List.of());
        }
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoomByNumber(String roomNumber) {
        Room room = roomRepository.findByNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));
                
        List<Booking> bookings = bookingRepository.findByRoomId(room.getId());
        for(Booking booking : bookings) {
            bookingRepository.delete(booking);
        }
        
        roomRepository.delete(room);
    }
    

    public List<Room> searchRooms(String type, Double maxPrice) {
        if (maxPrice != null) {
            return roomRepository.findAvailableRoomsByMaxPrice(maxPrice);
        }
        if (type != null) {
            return roomRepository.findByType(type);
        }
        return getAllRooms();
    }
}