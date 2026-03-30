package com.hotel.management.service;

import com.hotel.management.entity.Guest;
import com.hotel.management.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    public Guest saveGuest(Guest guest) {
        // Validate if email exists for a different guest
        guestRepository.findByEmail(guest.getEmail()).ifPresent(existing -> {
            if (!existing.getGuestId().equals(guest.getGuestId())) {
                throw new RuntimeException("A guest with this email already exists.");
            }
        });

        // Validate if phone exists for a different guest
        guestRepository.findByPhone(guest.getPhone()).ifPresent(existing -> {
            if (!existing.getGuestId().equals(guest.getGuestId())) {
                throw new RuntimeException("A guest with this phone number already exists.");
            }
        });

        // Prevent cascade error by ensuring the old relationship collection is preserved
        if (guest.getGuestId() != null) {
            Guest existing = guestRepository.findById(guest.getGuestId()).orElse(null);
            if (existing != null) {
                guest.setReservations(existing.getReservations());
            }
        }

        return guestRepository.save(guest);
    }

    public Guest getGuestById(Long id) {
        return guestRepository.findById(id).orElse(null);
    }


    @org.springframework.transaction.annotation.Transactional
    public void deleteGuest(Long id) {
        Guest guest = getGuestById(id);
        if (guest != null) {
            // guest.getReservations() will trigger delete due to CascadeType.ALL + orphanRemoval
            guestRepository.delete(guest);
        }
    }
}
