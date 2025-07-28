package com.example.restaurant.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ServeItemDto {
    private String foodName;
    private int quantity;
    private String specialInstructions;
}
