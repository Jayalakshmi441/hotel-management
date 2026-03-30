package com.hotel.management.entity;

import jakarta.validation.constraints.Size;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Double salary;

    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    @Column(unique = true, nullable = false)
    private String phone;
}
