package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(unique = true, nullable = false)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private Double pricePerNight;

    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.Available;

    public enum RoomType {
        Standard, Deluxe, Suite, Executive
    }

    public enum RoomStatus {
        Available, Occupied, Maintenance, Out_of_Service
    }
}
