package com.example.restaurant.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class DeliveryGroupDto {
    private Integer groupId;
    private Integer serveOrder;
    private String courseType;
    private String customerId;
    private List<ServeItemDto> items;
    private OrderLocationDto deliveryLocation;

}