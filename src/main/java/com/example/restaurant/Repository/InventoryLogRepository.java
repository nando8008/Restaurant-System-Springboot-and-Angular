package com.example.restaurant.Repository;


import com.example.restaurant.Entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Integer> {
}