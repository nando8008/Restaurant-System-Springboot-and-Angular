package com.example.restaurant.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "food_ingredients", schema = "restaurant_db")
public class FoodIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "food_id", referencedColumnName = "id")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", referencedColumnName = "id")
    private Ingredient ingredient;

    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity;
}