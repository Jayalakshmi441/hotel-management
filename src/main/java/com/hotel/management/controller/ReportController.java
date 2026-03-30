package com.hotel.management.controller;

import com.hotel.management.service.PaymentService;
import com.hotel.management.service.ServiceUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    @Autowired
    private com.hotel.management.repository.ReservationReportRepository reservationReportRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ServiceUsageService serviceUsageService;

    @GetMapping("/reports")
    public String showReports(Model model) {
        // Report 1: Reservations with Guest and Room details (Fetched from Database
        // View)
        model.addAttribute("detailedReservations", reservationReportRepository.findAll());

        // Report 2: Payments with Reservation details (JOIN logic)
        model.addAttribute("allPayments", paymentService.getAllPayments());

        // Report 3: Service Usage per Booking (JOIN logic)
        model.addAttribute("serviceUsages", serviceUsageService.getAllServiceUsages());

        return "reports";
    }
}
