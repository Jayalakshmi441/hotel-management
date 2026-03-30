package com.hotel.management.controller;

import com.hotel.management.entity.Guest;
import com.hotel.management.entity.Payment;
import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.BookingStatus;
import com.hotel.management.repository.GuestRepository;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.service.ReservationService;
import com.hotel.management.service.RoomService;
import com.hotel.management.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/dashboard")
    public String userDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<Guest> guestOpt = guestRepository.findByEmail(username);
        model.addAttribute("username", username);

        if (guestOpt.isPresent()) {
            Guest guest = guestOpt.get();
            model.addAttribute("guest", guest);
            List<Reservation> reservations = reservationRepository.findByGuest_Email(username);
            model.addAttribute("reservations", reservations);
        } else {
            model.addAttribute("guest", null);
            model.addAttribute("reservations", List.of());
        }
        return "user-dashboard";
    }

    @GetMapping("/book")
    public String showBookingForm(Authentication authentication, Model model) {
        String username = authentication.getName();
        Guest guest = guestRepository.findByEmail(username).orElse(null);
        if (guest == null)
            return "redirect:/user/dashboard";

        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setCheckInDate(LocalDate.now());
        reservation.setCheckOutDate(LocalDate.now().plusDays(1));

        model.addAttribute("reservation", reservation);
        model.addAttribute("rooms", roomService.getAvailableRooms());
        return "user-reservation-form";
    }

    @PostMapping("/book")
    public String processBooking(
            @org.springframework.web.bind.annotation.ModelAttribute("reservation") Reservation reservation,
            Authentication authentication,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        // 1. Date Validation
        if (reservation.getCheckOutDate().isBefore(reservation.getCheckInDate())) {
            throw new RuntimeException("Checkout date cannot be before checkin date");
        }

        String username = authentication.getName();
        Guest guest = guestRepository.findByEmail(username).orElseThrow();

        // 2. Security Sync
        reservation.setGuest(guest);
        reservation.setBookingStatus(BookingStatus.CONFIRMED);

        // 3. Save Reservation
        Reservation savedReservation = reservationService.saveReservation(reservation);

        // 4. Create Payment Automatic Receipt
        Payment payment = new Payment();
        payment.setReservation(savedReservation);
        payment.setAmount(savedReservation.getSafeTotalAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.Card);
        payment.setPaymentDate(java.time.LocalDateTime.now());
        paymentService.savePayment(payment);

        redirectAttributes.addFlashAttribute("success", "Your room has been booked successfully!");
        return "redirect:/user/dashboard";
    }
}
