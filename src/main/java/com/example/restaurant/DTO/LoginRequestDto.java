package com.example.restaurant.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
