package com.example.restaurant.Repository;

import com.example.restaurant.Entity.Kitchen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KitchenRepository extends JpaRepository<Kitchen, Integer> {
    List<Kitchen> findByStatus(String status);
    List<Kitchen> findByChefId(String chefId);

    @Query("""
        SELECT k FROM Kitchen k
        JOIN FETCH k.item i
        JOIN FETCH i.group g
        JOIN FETCH g.order o
        WHERE k.status = 'PREPARING' OR k.status = 'PENDING'
    """)
    List<Kitchen> getKitchenData();

}