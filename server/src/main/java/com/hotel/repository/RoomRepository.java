package com.hotel.repository;

import com.hotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByType(String type);
    Optional<Room> findByNumber(String number);
    @Query("SELECT r FROM Room r WHERE r.price <= :maxPrice")
    List<Room> findAvailableRoomsByMaxPrice(double maxPrice);
}