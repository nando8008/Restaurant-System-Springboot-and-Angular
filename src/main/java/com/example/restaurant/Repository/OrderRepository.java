package com.example.restaurant.Repository;

import com.example.restaurant.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByStatus(String status);

    @Query("SELECT o FROM Order o WHERE o.table.id = :tableId AND o.status = :status")
    List<Order> findByTableIdAndStatus(@Param("tableId") Integer tableId, @Param("status") String status);

    @Query("SELECT o FROM Order o WHERE o.orderById = :orderById")
    List<Order> findByCustomerId(@Param("orderById") String orderById);



}