package com.example.restaurant.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "employees", schema = "restaurant_db")
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;
    private String name;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean active = true;
    private Timestamp createdAt;

    public enum Role {
        ADMIN, MANAGER, WAITER, CHEF
    }
}
