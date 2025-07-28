package com.example.restaurant.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class OrderItemDto {
    private Integer id;
    private Integer quantity;
    private String specialInstructions;
    private Boolean isPrepared;
    private FoodDto food;

}