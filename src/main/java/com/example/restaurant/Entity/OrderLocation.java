package com.example.restaurant.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_location")
public class OrderLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String name;
    private String phone;
    private String street;
    private String apartment;
    private String city;
    private String state;
    private String zip;

    @Column(name = "delivery_instructions")
    private String deliveryInstructions;

}