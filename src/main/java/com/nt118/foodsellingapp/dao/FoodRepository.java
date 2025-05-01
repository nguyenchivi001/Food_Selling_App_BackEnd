package com.nt118.foodsellingapp.dao;

import com.nt118.foodsellingapp.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
    Page<Food> findByNameContaining(String name, Pageable pageable);
    Page<Food> findByCategoryId(int categoryId, Pageable pageable);
    Page<Food> findByNameContainingAndCategoryId(String name, int categoryId, Pageable pageable);
} 