package com.hotel.controller;

import com.hotel.model.Room;
import com.hotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/available")
    public List<Room> getAvailableRooms(@RequestParam(required = false) String type) {
        return roomService.getAvailableRooms(type);
    }

    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        return roomService.createRoom(room);
    }

    @PutMapping("/number/{roomNumber}")
    public ResponseEntity<Room> updateRoomByNumber(@PathVariable String roomNumber, @RequestBody Room roomDetails) {
        Room updatedRoom = roomService.updateRoomByNumber(roomNumber, roomDetails);
        return ResponseEntity.ok(updatedRoom);
    }
    @DeleteMapping("/number/{roomNumber}")
    public ResponseEntity<?> deleteRoomByNumber(@PathVariable String roomNumber) {
        roomService.deleteRoomByNumber(roomNumber);
        return ResponseEntity.ok().build();
    }
}