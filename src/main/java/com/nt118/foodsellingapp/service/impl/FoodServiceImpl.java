package com.nt118.foodsellingapp.service.impl;

import com.nt118.foodsellingapp.entity.Food;
import com.nt118.foodsellingapp.exception.ResourceNotFoundException;
import com.nt118.foodsellingapp.dao.FoodRepository;
import com.nt118.foodsellingapp.service.FoodService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
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

    @Override
    public Page<Food> findAll(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }

    @Override
    public Food findById(int id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));
    }

    @Override
    public Food save(Food food) {
        return foodRepository.save(food);
    }

    @Override
    public void deleteById(int id) {
        Food food = findById(id);
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
    public Page<Food> search(String name, Integer categoryId, Pageable pageable) {
        if (name != null && categoryId != null) {
            return foodRepository.findByNameContainingAndCategoryId(name, categoryId, pageable);
        } else if (name != null) {
            return foodRepository.findByNameContaining(name, pageable);
        } else if (categoryId != null) {
            return foodRepository.findByCategoryId(categoryId, pageable);
        }
        return foodRepository.findAll(pageable);
    }

    @Override
    public String uploadImage(int id, MultipartFile file) {
        Food food = findById(id);
        
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
