package com.example.restaurant.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "order_item_groups", schema = "restaurant_db")
public class OrderItemGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "course_type", length = 50)
    private String courseType;

    @Column(name = "serve_order")
    private Integer serveOrder = 0;

    @Column(name = "is_ready")
    private Boolean isReady = false;

    @Column(name = "delivered")
    private Boolean delivered = false;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> orderItems;
}