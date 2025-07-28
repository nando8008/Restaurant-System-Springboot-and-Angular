package com.example.restaurant.Controller;


import com.example.restaurant.Entity.Food;
import com.example.restaurant.Service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping("/all")
    public List<Food> getAllFood(){
        return foodService.getAllFood();
    }

    @GetMapping("/{category}")
    public List<Food> getAvailableFoodByCategory(@PathVariable String category) {
        List<Food> foods;

        if ("All".equalsIgnoreCase(category)) {
            foods = foodService.getAllFood();
        } else {
            foods = foodService.getFoodByCategory(category);
        }

        return foods.stream()
                .filter(Food::getIsAvailable)
                .collect(Collectors.toList());
    }



}
