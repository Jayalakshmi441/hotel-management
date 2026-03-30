package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "reservation_report_view")
@Immutable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationReport {

    @Id
    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "booking_status")
    private String bookingStatus;
}
