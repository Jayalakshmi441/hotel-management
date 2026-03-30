package com.hotel.management.repository;

import com.hotel.management.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

        // ✅ Total Revenue (excluding cancelled bookings)
        @Query("SELECT COALESCE(SUM(r.totalAmount), 0) " +
                        "FROM Reservation r " +
                        "WHERE r.bookingStatus <> 'CANCELLED'")
        Double getTotalRevenue();

        // ✅ Today's Revenue (dynamic date passed from service)
        @Query("SELECT COALESCE(SUM(r.totalAmount), 0) " +
                        "FROM Reservation r " +
                        "WHERE r.checkInDate = :today " +
                        "AND r.bookingStatus <> 'CANCELLED'")
        Double getTodayRevenue(@Param("today") LocalDate today);

        java.util.List<Reservation> findByGuest_Email(String email);

        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.transaction.annotation.Transactional
        @org.springframework.data.jpa.repository.Query("DELETE FROM Reservation r WHERE r.guest.guestId = :id")
        void deleteByGuestId(@org.springframework.data.repository.query.Param("id") Long id);

        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.transaction.annotation.Transactional
        @org.springframework.data.jpa.repository.Query("DELETE FROM Reservation r WHERE r.room.roomId = :id")
        void deleteByRoomId(@org.springframework.data.repository.query.Param("id") Long id);
}