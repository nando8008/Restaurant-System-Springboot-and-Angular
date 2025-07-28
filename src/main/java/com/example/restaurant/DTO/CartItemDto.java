package com.example.restaurant.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class CartItemDto {

    private Integer id;
    private Integer foodId;
    private String foodName;
    private Integer quantity;
    private String specialInstructions;
    private BigDecimal unitPrice;
    private LocalDateTime addedAt;

    public CartItemDto(Integer id, Integer foodId, String foodName, Integer quantity,
                       String specialInstructions, BigDecimal unitPrice, LocalDateTime addedAt) {
        this.id = id;
        this.foodId = foodId;
        this.foodName = foodName;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
        this.unitPrice = unitPrice;
        this.addedAt = addedAt;
    }

}
