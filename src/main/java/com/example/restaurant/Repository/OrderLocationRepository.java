package com.example.restaurant.Repository;

import com.example.restaurant.Entity.OrderLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLocationRepository extends JpaRepository<OrderLocation,Integer> {

}
