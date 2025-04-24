package com.nt118.foodsellingapp.service;

import com.nt118.foodsellingapp.entity.Food;

import java.util.List;

public interface FoodService {
    List<Food> findAll();

    Food findById(int theId);

    Food save(Food theFood);

    void deleteById(int theId);

}
