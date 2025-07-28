package com.example.restaurant.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ingredients", schema = "restaurant_db")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "current_stock", precision = 10, scale = 2)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(name = "threshold", precision = 10, scale = 2)
    private BigDecimal threshold;

    @Column(name = "created_at", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}