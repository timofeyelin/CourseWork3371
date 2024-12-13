package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.BookingRepository;
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

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public List<Room> getAvailableRooms(String type) {
        if (type != null && !type.isEmpty()) {
            return roomRepository.findByTypeAndIsAvailable(type, true);
        }
        return roomRepository.findAll().stream()
                .filter(Room::isAvailable)
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
    public Room updateRoom(Room room) {
        if (!roomRepository.existsById(room.getId())) {
            throw new IllegalArgumentException("Room not found with id: " + room.getId());
        }
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (bookingRepository.findByRoomId(id).isEmpty()) {
            roomRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Cannot delete room with existing bookings");
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