package com.hotel.management.controller;

import com.hotel.management.entity.Room;
import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.BookingStatus;
import com.hotel.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

        @Autowired
        private GuestService guestService;

        @Autowired
        private RoomService roomService;

        @Autowired
        private ReservationService reservationService;

        @Autowired
        private StaffService staffService;

        @GetMapping("/dashboard")
        public String dashboard(Model model) {

                LocalDate today = LocalDate.now();
                List<Reservation> allReservations = reservationService.getAllReservations();
                System.out.println("TOTAL RESERVATIONS: " + allReservations.size());
                List<Room> allRooms = roomService.getAllRooms();

                // 1️⃣ Basic Counts
                model.addAttribute("guestCount", guestService.getAllGuests().size());
                model.addAttribute("roomCount", allRooms.size());
                model.addAttribute("availableRooms", roomService.getAvailableRooms().size());
                model.addAttribute("occupiedRooms",
                                allRooms.size() - roomService.getAvailableRooms().size());
                model.addAttribute("staffCount", staffService.getAllStaff().size());

                // 2️⃣ Revenue
                model.addAttribute("totalRevenue", reservationService.getTotalRevenue());
                model.addAttribute("todayRevenue", reservationService.getTodayRevenue());

                // 3️⃣ Average Room Price
                double avgPrice = allRooms.stream()
                                .mapToDouble(Room::getPricePerNight)
                                .average()
                                .orElse(0.0);
                model.addAttribute("avgRoomPrice", avgPrice);

                // 4️⃣ Most Booked Room Type
                String mostBookedType = allReservations.stream()
                                .filter(r -> r.getRoom() != null)
                                .collect(Collectors.groupingBy(
                                                r -> r.getRoom().getRoomType().name(),
                                                Collectors.counting()))
                                .entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse("None");

                model.addAttribute("mostBookedType", mostBookedType);

                // 5️⃣ Recent Reservations
                model.addAttribute("recentReservations",
                                allReservations.stream()
                                                .sorted((r1, r2) -> Long.compare(
                                                                r2.getReservationId(),
                                                                r1.getReservationId()))
                                                .limit(5)
                                                .collect(Collectors.toList()));

                // 6️⃣ Revenue Trend (Professional Per-Day Logic)
                List<String> labels = new ArrayList<>();
                List<Double> revenueData = new ArrayList<>();

                for (int i = 6; i >= 0; i--) {

                        LocalDate date = today.minusDays(i);
                        labels.add(date.getDayOfWeek().name().substring(0, 3)
                                        + " " + date.getDayOfMonth());

                        double dailyRevenue = allReservations.stream()
                                        .filter(r -> r.getCheckInDate() != null
                                                        && r.getCheckOutDate() != null)
                                        .filter(r -> !r.getCheckInDate().isAfter(date)
                                                        && r.getCheckOutDate().isAfter(date))
                                        .filter(r -> r.getBookingStatus() != BookingStatus.CANCELLED)
                                        .mapToDouble(r -> {

                                                long days = ChronoUnit.DAYS.between(
                                                                r.getCheckInDate(),
                                                                r.getCheckOutDate());

                                                if (days <= 0)
                                                        days = 1;

                                                return r.getSafeTotalAmount() / days;

                                        }).sum();

                        revenueData.add(dailyRevenue);
                }

                model.addAttribute("revenueLabels", labels);
                model.addAttribute("revenueData", revenueData);

                // 7️⃣ Room Category Distribution
                Map<String, Long> categoryCounts = allRooms.stream()
                                .collect(Collectors.groupingBy(
                                                r -> r.getRoomType().name(),
                                                Collectors.counting()));

                model.addAttribute("categoryLabels", categoryCounts.keySet());
                model.addAttribute("categoryData", categoryCounts.values());
                // 7. Financial Settlement Ledger (recent 10 transactions)
                List<Reservation> ledgerData = allReservations.stream()
                                .filter(r -> r.getBookingStatus() != null
                                                && r.getBookingStatus() != BookingStatus.CANCELLED)
                                .sorted((r1, r2) -> {
                                        if (r2.getReservationId() != null && r1.getReservationId() != null)
                                                return Long.compare(r2.getReservationId(), r1.getReservationId());
                                        return 0;
                                })
                                .limit(10)
                                .collect(Collectors.toList());

                model.addAttribute("ledgerData", ledgerData);

                return "dashboard";
        }
}