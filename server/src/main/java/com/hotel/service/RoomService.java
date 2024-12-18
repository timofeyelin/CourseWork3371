package com.hotel.service;

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

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public Optional<Room> getRoomByNumber(String number) {
        return roomRepository.findByNumber(number);
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
    public Room updateRoom(Long id, Room roomDetails) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));

        room.setType(roomDetails.getType());
        room.setPrice(roomDetails.getPrice());
        if (roomDetails.getDescription() != null) {
            room.setDescription(roomDetails.getDescription());
        }
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (bookingRepository.findByRoomId(id).isEmpty()) {
            roomRepository.deleteById(id);
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