package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = true)
    private Room room;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    private Double totalAmount;

    /**
     * Safely returns the total amount. If the trigger-calculated value is not found
     * in memory,
     * it calculates it on the fly: (Check-out - Check-in) * Room Price.
     */
    @Transient
    public Double getSafeTotalAmount() {
        if (totalAmount != null && totalAmount > 0) {
            return totalAmount;
        }
        if (room != null && checkInDate != null && checkOutDate != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            if (days <= 0)
                days = 1; // Minimum 1 day charge
            return days * room.getPricePerNight();
        }
        return 0.0;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus = BookingStatus.CONFIRMED;

    public BookingStatus getBookingStatus() {
        return bookingStatus == null ? BookingStatus.CONFIRMED : bookingStatus;
    }

    public void setStatus(BookingStatus status) {
        this.bookingStatus = status;
    }

    public BookingStatus getStatus() {
        return getBookingStatus();
    }

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private java.util.List<Payment> payments = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private java.util.List<ServiceUsage> serviceUsages = new java.util.ArrayList<>();

    @Transient
    public Double getBalance() {
        Double total = getSafeTotalAmount();
        Double paid = 0.0;
        if (payments != null) {
            for (Payment p : payments) {
                if (p.getAmount() != null) {
                    paid += p.getAmount();
                }
            }
        }
        return Math.max(0.0, total - paid);
    }
}
