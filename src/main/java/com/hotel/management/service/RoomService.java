package com.hotel.management.service;

import com.hotel.management.entity.Room;
import com.hotel.management.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(Room.RoomStatus.Available);
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Autowired
    private com.hotel.management.repository.ReservationRepository reservationRepository;

    @org.springframework.transaction.annotation.Transactional
    public void deleteRoom(Long id) {
        System.out.println("DEBUG: RoomService.deleteRoom called for ID: " + id);
        Room room = getRoomById(id);
        if (room != null) {
            java.util.List<com.hotel.management.entity.Reservation> reservations = reservationRepository.findAll().stream()
                    .filter(r -> r.getRoom() != null && r.getRoom().getRoomId().equals(id))
                    .collect(java.util.stream.Collectors.toList());

            System.out.println("DEBUG: Found " + reservations.size() + " reservations for room " + id);

            if (reservations != null && !reservations.isEmpty()) {
                reservationRepository.deleteAll(reservations);
                System.out.println("DEBUG: Deleted reservations for room " + id);
            }

            roomRepository.delete(room);
            System.out.println("DEBUG: Deleted room " + id);
        } else {
            System.out.println("DEBUG: Room " + id + " not found!");
        }
    }
}
