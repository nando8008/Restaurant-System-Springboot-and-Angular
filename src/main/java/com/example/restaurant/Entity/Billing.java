package com.example.restaurant.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "billing", schema = "restaurant_db")
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "placed_by_id", nullable = false, unique = true)
    private String placedById;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @Column(name = "tax", nullable = false)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "discount", nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "service_charge", nullable = false)
    private BigDecimal serviceCharge = BigDecimal.ZERO;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @Column(name = "payment_mode", length = 20)
    private String paymentMode;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "UNPAID";

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}