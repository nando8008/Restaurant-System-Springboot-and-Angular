package com.example.restaurant.Service;


import com.example.restaurant.Entity.Food;
import com.example.restaurant.Entity.Order;
import com.example.restaurant.Repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FoodService {


    @Autowired
    private FoodRepository foodRepository;

    public List<Food> getAllFood() {
        return foodRepository.getAllInCategoryOrder();
    }

    public List<Food> getFoodByCategory(String category) {
        return foodRepository.findByCategoryIgnoreCase(category);
    }
}
