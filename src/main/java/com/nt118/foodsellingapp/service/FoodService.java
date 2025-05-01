package com.nt118.foodsellingapp.service;

import com.nt118.foodsellingapp.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FoodService {
    Page<Food> findAll(Pageable pageable);
    Food findById(int id);
    Food save(Food food);
    void deleteById(int id);
    Page<Food> search(String name, Integer categoryId, Pageable pageable);
    String uploadImage(int id, MultipartFile file);
}
