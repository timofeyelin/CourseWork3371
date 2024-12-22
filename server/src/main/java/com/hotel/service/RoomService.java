package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
        if (type != null && !type.isEmpty()) {
            rooms = roomRepository.findByTypeAndIsAvailable(type, true);
        } else {
            rooms = roomRepository.findAll();
        }

        return rooms.stream()
                .filter(room -> {
                    List<Booking> bookings = bookingRepository.findByRoomId(room.getId());
                    return bookings.isEmpty() && room.isAvailable();
                })
                .toList();
    }

    @Transactional
    public Room createRoom(Room room) {
        if (room.getNumber() == null || room.getNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        room.setAvailable(true);
        return roomRepository.save(room);
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
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoomByNumber(String roomNumber) {
        Room room = roomRepository.findByNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));
        if (bookingRepository.findByRoomId(room.getId()).isEmpty()) {
            roomRepository.delete(room);
        } else {
            throw new IllegalStateException("Нельзя удалить номер с существующими бронированиями");
        }
    }
    

    public List<Room> searchRooms(String type, Double maxPrice, Boolean isAvailable) {
        if (maxPrice != null) {
            return roomRepository.findAvailableRoomsByMaxPrice(maxPrice);
        }
        if (type != null && isAvailable != null) {
            return roomRepository.findByTypeAndIsAvailable(type, isAvailable);
        }
        return getAllRooms();
    }
}