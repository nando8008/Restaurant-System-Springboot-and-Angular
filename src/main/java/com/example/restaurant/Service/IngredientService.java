package com.example.restaurant.Service;

import com.example.restaurant.Entity.Ingredient;
import com.example.restaurant.Repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public Optional<Ingredient> getIngredient(Integer id) {
        return ingredientRepository.findById(id);
    }

    @Transactional
    public Ingredient updateStock(Integer id, BigDecimal change) throws IllegalArgumentException {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + id));

        BigDecimal newStock = ingredient.getCurrentStock().add(change);

        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stock cannot go below zero.");
        }

        ingredient.setCurrentStock(newStock);
        return ingredientRepository.save(ingredient);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }
}

