package com.example.restaurant.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class OrderItemGroupDto {
    private Integer id;
    private String courseType;
    private Integer serveOrder;
    private Boolean isReady;
    private Boolean delivered;
    private List<OrderItemDto> items;

}