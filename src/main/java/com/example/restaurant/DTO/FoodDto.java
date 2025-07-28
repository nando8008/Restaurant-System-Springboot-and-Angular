package com.example.restaurant.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class FoodDto {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String veg;
}