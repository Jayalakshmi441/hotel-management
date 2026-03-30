package com.hotel.management.controller;

import com.hotel.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class DiagnosticController {

    @Autowired
    private GuestRepository guestRepo;
    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private ReservationRepository resRepo;
    @Autowired
    private ReservationReportRepository reportRepo;

    @GetMapping("/api/diag")
    public Map<String, Object> getCounts() {
        return Map.of(
                "guests", guestRepo.count(),
                "rooms", roomRepo.count(),
                "reservations", resRepo.count(),
                "report_view_records", reportRepo.count());
    }
}
