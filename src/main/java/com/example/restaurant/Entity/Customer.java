package com.example.restaurant.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "customers", schema = "restaurant_db")
@Getter
@Setter
public class Customer {

    @Id
    private String id; // Now a string like "c1", "c2"

    private String name;
    private String email;
    private String phone;
    private String password;
    @Column(name = "joined_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp joinedAt;
}