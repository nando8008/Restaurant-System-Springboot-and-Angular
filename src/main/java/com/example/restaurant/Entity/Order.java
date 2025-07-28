package com.example.restaurant.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders", schema = "restaurant_db")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_by_id", nullable = false, length = 20)
    private String orderById;

    @Column(name = "order_by_type", nullable = false, length = 10)
    private String orderByType;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private Tables table;

    @Column(name = "order_time", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime orderTime;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;

    @Column(name = "status", length = 20)
    private String status = "PLACED";



    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItemGroup> orderItemGroups;
}