package com.hotel.management.service;

import com.hotel.management.entity.Payment;
import com.hotel.management.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByReservation(Long reservationId) {
        return paymentRepository.findByReservation_ReservationId(reservationId);
    }
}
