package com.example.restaurant.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Data
public class ServeGroupDto {
    private Integer groupId;
    private Integer serveOrder;
    private String courseType;
    private Integer tableNumber;
    private List<ServeItemDto> items;
}
