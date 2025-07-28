package com.example.restaurant.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class KitchenDto {
    private Integer id;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String chefId;

    private Integer quantity;
    private String specialInstructions;
    private Boolean isPrepared;
    private String foodName;
    private String courseType;
    private Integer serveOrder;
    private Boolean isReady;
    private Boolean delivered;
    private Integer tableNumber;
}
