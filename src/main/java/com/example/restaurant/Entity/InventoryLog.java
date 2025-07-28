package com.example.restaurant.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inventory_logs", schema = "restaurant_db")
public class InventoryLog {

    public enum Reason {
        PURCHASE,
        USAGE,
        WASTE,
        ADJUSTMENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", referencedColumnName = "id")
    private Ingredient ingredient;

    @Column(name = "quantity_change", precision = 10, scale = 2)
    private BigDecimal quantityChange;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private Reason reason;

    @Column(name = "log_time", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime logTime;
}