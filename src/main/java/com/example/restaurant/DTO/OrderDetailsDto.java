package com.example.restaurant.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
public class OrderDetailsDto {
    private Integer id;
    private String orderById;
    private String orderByType;
    private Integer tableId;
    private LocalDateTime orderTime;
    private LocalDateTime completionTime;
    private String status;
    private List<OrderItemGroupDto> groups;
}