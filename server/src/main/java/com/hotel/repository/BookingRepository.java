package com.hotel.repository;

import com.hotel.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByRoomId(Long roomId);  // Добавляем этот метод

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
            "((b.startDate BETWEEN :start AND :end) OR " +
            "(b.endDate BETWEEN :start AND :end))")
    List<Booking> findOverlappingBookings(Long roomId, LocalDateTime start, LocalDateTime end);
}