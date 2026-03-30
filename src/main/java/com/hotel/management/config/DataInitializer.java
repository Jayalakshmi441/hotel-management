package com.hotel.management.config;

import com.hotel.management.entity.*;
import com.hotel.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 0. Data Migration: Update old camelCase statuses to UPPERCASE
        try {
            jdbcTemplate.execute("ALTER TABLE reservations MODIFY booking_status VARCHAR(255)");
            jdbcTemplate.execute("UPDATE reservations SET booking_status = 'CONFIRMED' WHERE booking_status LIKE '%onfirmed%'");
            jdbcTemplate.execute("UPDATE reservations SET booking_status = 'CANCELLED' WHERE booking_status LIKE '%ancel%'");
            jdbcTemplate.execute("UPDATE reservations SET booking_status = 'CHECKED_IN' WHERE booking_status LIKE '%hecked%n%'");
            jdbcTemplate.execute("UPDATE reservations SET booking_status = 'CHECKED_OUT' WHERE booking_status LIKE '%hecked%ut%'");

            jdbcTemplate.execute("ALTER TABLE rooms MODIFY status VARCHAR(255)");
            jdbcTemplate.execute("UPDATE rooms SET status = 'Available' WHERE status LIKE '%vailable%'"); 
            jdbcTemplate.execute("UPDATE rooms SET status = 'Occupied' WHERE status LIKE '%ccupied%'");
            System.out.println("Data migration (case update) successful.");
        } catch (Exception e) {
            System.out.println("Migration warning (might be already modified): " + e.getMessage());
        }

        // 1. Create Default Auth Users if they don't exist
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);

            User testUser = new User();
            testUser.setUsername("testuser@example.com");
            testUser.setPassword(passwordEncoder.encode("user123"));
            testUser.setRole("ROLE_USER");
            userRepository.save(testUser);
            System.out.println("Default auth users created.");
        }

        // 2. Ensure Guest profile exists for testuser@example.com
        Guest guest = guestRepository.findByEmail("testuser@example.com").orElse(null);
        if (guest == null) {
            guest = new Guest();
            guest.setName("John Doe");
            guest.setEmail("testuser@example.com");
            guest.setPhone("9876543210");
            guest.setAddress("123, Green Street, City");
            guest.setIdProof("Aadhaar Card");
            guest = guestRepository.save(guest);
            System.out.println("Guest profile created for testuser@example.com");
        }

        // 3. Ensure at least one room exists for demo
        Room room = roomRepository.findAll().stream().findFirst().orElse(null);
        if (room == null) {
            room = new Room();
            room.setRoomNumber("101");
            room.setRoomType(Room.RoomType.Deluxe);
            room.setPricePerNight(2500.0);
            room.setStatus(Room.RoomStatus.Available);
            room = roomRepository.save(room);
        }

        // 4. Create sample reservation for demo if guest has none
        if (reservationRepository.findByGuest_Email("testuser@example.com").isEmpty()) {
            Reservation res = new Reservation();
            res.setGuest(guest);
            res.setRoom(room);
            res.setCheckInDate(LocalDate.now());
            res.setCheckOutDate(LocalDate.now().plusDays(2));
            res.setBookingStatus(BookingStatus.CONFIRMED);
            res.setTotalAmount(5000.0);
            reservationRepository.save(res);
            System.out.println("Sample reservation created for testuser@example.com");
        }
    }
}
