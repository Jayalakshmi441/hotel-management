package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usageId;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(nullable = false)
    private Integer quantity = 1;

    private Double totalCost;
}
