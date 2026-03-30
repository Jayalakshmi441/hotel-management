package com.hotel.management.service;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.BookingStatus;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;


    // ✅ Get all reservations
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // ✅ Save reservation with correct total calculation
    public Reservation saveReservation(Reservation reservation) {

        if (reservation.getRoom() != null && reservation.getRoom().getRoomId() != null) {

            var room = roomRepository
                    .findById(reservation.getRoom().getRoomId())
                    .orElse(null);

            if (room != null) {

                reservation.setRoom(room);

                if (reservation.getCheckInDate() != null &&
                        reservation.getCheckOutDate() != null) {

                    long days = ChronoUnit.DAYS.between(
                            reservation.getCheckInDate(),
                            reservation.getCheckOutDate());

                    if (days <= 0)
                        days = 1;

                    Double totalAmount = days * room.getPricePerNight();
                    reservation.setTotalAmount(totalAmount);
                }
            }
        }

        return reservationRepository.save(reservation);
    }

    // ✅ Get reservation by ID
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    // ✅ Cancel reservation
    @org.springframework.transaction.annotation.Transactional
    public void cancelReservation(Long id) {
        Reservation res = getReservationById(id);
        if (res != null) {
            res.setBookingStatus(BookingStatus.CANCELLED);
            reservationRepository.save(res);
        }
    }

    // ✅ Delete reservation
    @org.springframework.transaction.annotation.Transactional
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    // ✅ Total Revenue (dynamic from DB)
    public Double getTotalRevenue() {
        Double total = reservationRepository.getTotalRevenue();
        return total != null ? total : 0.0;
    }

    // ✅ Today's Revenue (dynamic date passed properly)
    public Double getTodayRevenue() {
        Double today = reservationRepository.getTodayRevenue(LocalDate.now());
        return today != null ? today : 0.0;
    }
}