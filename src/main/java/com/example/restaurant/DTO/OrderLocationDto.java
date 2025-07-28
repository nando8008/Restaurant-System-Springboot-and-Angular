package com.example.restaurant.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class OrderLocationDto {

    private Integer orderId;
    private String name;
    private String phone;
    private String street;
    private String apartment;
    private String city;
    private String state;
    private String zip;
    private String deliveryInstructions;
}
