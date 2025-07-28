package com.example.restaurant.DTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class OrderRequestDto {

    private String orderByType;
    private Integer tableId;
    private List<ItemDTO> items;

    @Getter
    @Setter
    public static class ItemDTO {
        private Integer foodId;
        private Integer quantity;
        private String courseType;
        private String specialInstructions;
    }
}
