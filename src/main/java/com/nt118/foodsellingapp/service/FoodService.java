package com.nt118.foodsellingapp.service;

import com.nt118.foodsellingapp.dto.FoodDTO;
import com.nt118.foodsellingapp.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FoodService {
    Page<FoodDTO> findAll(Pageable pageable);
    FoodDTO findById(int id);
    FoodDTO save(Food food);
    void deleteById(int id);
    Page<FoodDTO> search(String name, Integer categoryId, Pageable pageable);
    String uploadImage(int id, MultipartFile file);
}
