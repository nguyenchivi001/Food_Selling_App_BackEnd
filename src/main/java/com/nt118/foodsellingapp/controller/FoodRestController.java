package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.entity.Food;
import com.nt118.foodsellingapp.service.FoodService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller
@RestController
@RequestMapping("/api")
public class FoodRestController {
    private FoodService foodService;

    public FoodRestController(FoodService theFoodService) {
        foodService = theFoodService;
    }

    // add mapping for "/list"
    @GetMapping("/foods")
    public List<Food> listFoods() {
        // get the food from db
        List<Food> theFoods = foodService.findAll();

        return theFoods;
    }

    @PostMapping("/foods")
    public Food addFood(@RequestBody Food newFood) {
        return foodService.save(newFood);
    }
}
