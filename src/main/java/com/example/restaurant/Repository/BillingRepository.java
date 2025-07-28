package com.example.restaurant.Repository;

import com.example.restaurant.Entity.Billing;
import com.example.restaurant.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillingRepository extends JpaRepository<Billing, Integer> {
    Billing findByPlacedById(String placedById);
    List<Billing> findByOrderIdIn(List<Integer> orderIds);
    List<Billing> findByOrderIn(List<Order> orders);
    Optional<Billing> findByOrderId(Integer orderId);
}
