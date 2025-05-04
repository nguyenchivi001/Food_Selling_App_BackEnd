package com.nt118.foodsellingapp.mapper;

import com.nt118.foodsellingapp.dto.FoodDTO;
import com.nt118.foodsellingapp.entity.Food;
import org.springframework.stereotype.Component;

@Component
public class FoodMapper {
    
    public FoodDTO toDTO(Food food) {
        if (food == null) {
            return null;
        }
        
        FoodDTO foodDTO = new FoodDTO();
        foodDTO.setId(food.getId());
        foodDTO.setName(food.getName());
        foodDTO.setDescription(food.getDescription());
        foodDTO.setPrice(food.getPrice());
        foodDTO.setImageFilename(food.getImageFilename());
        foodDTO.setStockQuantity(food.getStockQuantity());
        foodDTO.setAvailable(food.isAvailable());
        
        if (food.getCategory() != null) {
            foodDTO.setCategoryId(food.getCategory().getId());
            foodDTO.setCategoryName(food.getCategory().getName());
        }
        
        return foodDTO;
    }
    
    public Food toEntity(FoodDTO foodDTO) {
        if (foodDTO == null) {
            return null;
        }
        
        Food food = new Food();
        food.setId(foodDTO.getId());
        food.setName(foodDTO.getName());
        food.setDescription(foodDTO.getDescription());
        food.setPrice(foodDTO.getPrice());
        food.setImageFilename(foodDTO.getImageFilename());
        food.setStockQuantity(foodDTO.getStockQuantity());
        food.setAvailable(foodDTO.isAvailable());
        
        return food;
    }
} 