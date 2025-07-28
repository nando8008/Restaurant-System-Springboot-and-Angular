package com.example.restaurant.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BillRequestDto {
    private String placedById;
    private Integer tableId;
    private String paymentMode;
    private String discountCode;
    private boolean applyServiceCharge;
}
