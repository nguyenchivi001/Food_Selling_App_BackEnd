package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.ApiResponse;
import com.nt118.foodsellingapp.dto.FoodDTO;
import com.nt118.foodsellingapp.entity.Food;
import com.nt118.foodsellingapp.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
public class FoodRestController {
    private final FoodService foodService;
    private final Path rootLocation = Paths.get("src/main/resources/static/images");

    public FoodRestController(FoodService theFoodService) {
        foodService = theFoodService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FoodDTO>>> listFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toLowerCase());
        Page<FoodDTO> foods = foodService.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sortBy)));
        return ResponseEntity.ok(ApiResponse.success(foods));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodDTO>> getFood(@PathVariable int id) {
        FoodDTO food = foodService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(food));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodDTO>> addFood(@Valid @RequestBody Food food) {
        FoodDTO savedFood = foodService.save(food);
        return ResponseEntity.ok(ApiResponse.success(savedFood, "Food added successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodDTO>> updateFood(
            @PathVariable int id,
            @Valid @RequestBody Food food) {
        food.setId(id);
        FoodDTO updatedFood = foodService.save(food);
        return ResponseEntity.ok(ApiResponse.success(updatedFood, "Food updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFood(@PathVariable int id) {
        foodService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Food deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<FoodDTO>>> searchFoods(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FoodDTO> foods = foodService.search(name, categoryId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(foods));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<String>> uploadFoodImage(
            @PathVariable int id,
            @RequestParam("file") MultipartFile file) {
        String imageFilename = foodService.uploadImage(id, file);
        return ResponseEntity.ok(ApiResponse.success(imageFilename, "Image uploaded successfully"));
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            byte[] imageBytes = Files.readAllBytes(file);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
