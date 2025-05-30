package com.app.pandemicnet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "data_location")
@Getter
@Setter
public class DataLocation {
    // Explicit getters and setters for fields that might be accessed frequently
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unique_id", nullable = false, unique = true)
    private UUID uniqueId = UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Embedded
    private Address address;

}
