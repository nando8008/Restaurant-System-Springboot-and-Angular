package com.example.restaurant.Repository;

import com.example.restaurant.Entity.FoodIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodIngredientRepository extends JpaRepository<FoodIngredient, Integer> {
}