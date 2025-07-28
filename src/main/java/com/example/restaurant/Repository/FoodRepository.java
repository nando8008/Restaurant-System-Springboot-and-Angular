package com.example.restaurant.Repository;

import com.example.restaurant.Entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Integer> {
    List<Food> findByCategoryIgnoreCase(String category);

    @Query(value = """
    SELECT * FROM food
    ORDER BY
      CASE category
        WHEN 'starters' THEN 1
        WHEN 'soup' THEN 2
        WHEN 'pastas' THEN 3
        WHEN 'starters' THEN 4
        WHEN 'pizzas' THEN 5
        WHEN 'burgers' THEN 6
        WHEN 'bread' THEN 7
        WHEN 'curries' THEN 8
        WHEN 'rice' THEN 9
        WHEN 'dessert' THEN 10
        WHEN 'beverages' THEN 11
        ELSE 99
      END,
      id;""", nativeQuery = true)
    List<Food> getAllInCategoryOrder();

}