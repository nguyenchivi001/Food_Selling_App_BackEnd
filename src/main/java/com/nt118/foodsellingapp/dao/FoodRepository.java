package com.nt118.foodsellingapp.dao;

import com.nt118.foodsellingapp.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Integer> {
}
