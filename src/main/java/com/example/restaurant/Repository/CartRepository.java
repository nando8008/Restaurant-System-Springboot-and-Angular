package com.example.restaurant.Repository;

import com.example.restaurant.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findBySessionId(String sessionId);
}
