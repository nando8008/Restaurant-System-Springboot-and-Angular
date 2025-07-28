package com.example.restaurant.Repository;

import com.example.restaurant.DTO.CartItemDto;
import com.example.restaurant.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCartId(Integer cartId);
    void deleteByCartId(Integer cartId);

    @Query("SELECT new com.example.restaurant.DTO.CartItemDto(ci.id, f.id, f.name, ci.quantity, ci.specialInstructions, ci.unitPrice, ci.addedAt) " +
            "FROM CartItem ci JOIN ci.food f WHERE ci.cart.id = :cartId")
    List<CartItemDto> findCartItemDtosByCartId(@Param("cartId") Long cartId);

    Optional<CartItem> findByCartIdAndFoodId(Integer cartId, Integer foodId);

    void deleteByCartIdAndFoodId(Integer cartId, Integer foodId);

    List<CartItem> findByCartSessionId(String sessionId);
}