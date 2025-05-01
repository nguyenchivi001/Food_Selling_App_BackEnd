package com.nt118.foodsellingapp.service.impl;

import com.nt118.foodsellingapp.dto.FoodDTO;
import com.nt118.foodsellingapp.entity.Food;
import com.nt118.foodsellingapp.exception.ResourceNotFoundException;
import com.nt118.foodsellingapp.dao.FoodRepository;
import com.nt118.foodsellingapp.service.FoodService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
public class FoodServiceImpl implements FoodService {
    private final FoodRepository foodRepository;
    private final Path rootLocation = Paths.get("src/main/resources/static/images");

    public FoodServiceImpl(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    private FoodDTO convertToDTO(Food food) {
        if (food == null) {
            return null;
        }
        
        FoodDTO dto = new FoodDTO();
        dto.setId(food.getId());
        dto.setName(food.getName());
        dto.setDescription(food.getDescription());
        dto.setPrice(food.getPrice());
        dto.setImageFilename(food.getImageFilename());
        dto.setCategoryId(food.getCategory().getId());
        dto.setCategoryName(food.getCategory().getName());
        dto.setStockQuantity(food.getStockQuantity());
        dto.setAvailable(food.isAvailable());
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodDTO> findAll(Pageable pageable) {
        Page<Food> foods = foodRepository.findAll(pageable);
        return foods.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodDTO findById(int id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));
        return convertToDTO(food);
    }

    @Override
    @Transactional
    public FoodDTO save(Food food) {
        Food savedFood = foodRepository.save(food);
        return convertToDTO(savedFood);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));
        // Delete the image file if exists
        if (food.getImageFilename() != null) {
            try {
                Path file = rootLocation.resolve(food.getImageFilename());
                Files.deleteIfExists(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image file", e);
            }
        }
        foodRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodDTO> search(String name, Integer categoryId, Pageable pageable) {
        Page<Food> foods;
        if (name != null && categoryId != null) {
            foods = foodRepository.findByNameContainingAndCategoryId(name, categoryId, pageable);
        } else if (name != null) {
            foods = foodRepository.findByNameContaining(name, pageable);
        } else if (categoryId != null) {
            foods = foodRepository.findByCategoryId(categoryId, pageable);
        } else {
            foods = foodRepository.findAll(pageable);
        }
        return foods.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public String uploadImage(int id, MultipartFile file) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));
        
        // Delete old image if exists
        if (food.getImageFilename() != null) {
            try {
                Path oldFile = rootLocation.resolve(food.getImageFilename());
                Files.deleteIfExists(oldFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete old image file", e);
            }
        }

        // Generate new filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;

        try {
            // Save the file
            Path destinationFile = rootLocation.resolve(newFilename)
                    .normalize().toAbsolutePath();
            Files.copy(file.getInputStream(), destinationFile);

            // Update food entity
            food.setImageFilename(newFilename);
            foodRepository.save(food);

            return newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
