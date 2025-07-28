package com.example.restaurant.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "tables", schema = "restaurant_db")
public class Tables {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "table_number", nullable = false, unique = true)
    private Integer tableNumber;

    @Column(name = "capacity")
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.AVAILABLE;

    public enum Status {
        AVAILABLE,
        OCCUPIED,
        NEEDS_ATTENTION,
        RESERVED
    }
}