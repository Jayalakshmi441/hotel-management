package com.hotel.management.controller;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.BookingStatus;
import com.hotel.management.entity.Payment;
import com.hotel.management.service.GuestService;
import com.hotel.management.service.ReservationService;
import com.hotel.management.service.RoomService;
import com.hotel.management.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private GuestService guestService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "reservations";
    }

    @GetMapping("/new")
    public String showReservationForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("guests", guestService.getAllGuests());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("statuses", BookingStatus.values());
        return "reservation-form";
    }

    @PostMapping("/save")
    public String saveReservation(@ModelAttribute("reservation") Reservation reservation,
            RedirectAttributes redirectAttributes) {

        // 1. Date Validation
        if (reservation.getCheckOutDate().isBefore(reservation.getCheckInDate())) {
            throw new RuntimeException("Checkout date cannot be before checkin date");
        }

        // 2. Save reservation first
        Reservation savedReservation = reservationService.saveReservation(reservation);

        // 3. Automatically create payment record
        Payment payment = new Payment();
        payment.setReservation(savedReservation);
        // Use getSafeTotalAmount to ensure we have a valid price based on duration and
        // room price
        payment.setAmount(savedReservation.getSafeTotalAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.Card);
        payment.setPaymentDate(java.time.LocalDateTime.now());

        paymentService.savePayment(payment);

        redirectAttributes.addFlashAttribute("success", "Reservation and Payment processed successfully!");
        return "redirect:/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        System.out.println("DEBUG: Reached Edit Reservation Endpoint for ID: " + id);
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation != null) {
            model.addAttribute("reservation", reservation);
            model.addAttribute("guests", guestService.getAllGuests());
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("statuses", BookingStatus.values());
            return "reservation-form";
        }
        return "redirect:/reservations";
    }

    @PostMapping("/cancel/{id}")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: Reached Cancel Reservation Endpoint for ID: " + id);
        reservationService.cancelReservation(id);
        redirectAttributes.addFlashAttribute("success", "Reservation cancelled successfully!");
        return "redirect:/reservations";
    }
}