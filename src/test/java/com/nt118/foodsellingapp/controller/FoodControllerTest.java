package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.FoodDTO;
import com.nt118.foodsellingapp.dto.response.ApiResponse;
import com.nt118.foodsellingapp.entity.Food;
import com.nt118.foodsellingapp.service.FoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodControllerTest {

    @Mock
    private FoodService foodService;

    @InjectMocks
    private FoodController foodController;

    private Food food;
    private FoodDTO foodDTO;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        food = new Food();
        food.setId(1);
        food.setName("Pizza");
        food.setPrice(100.0);

        foodDTO = new FoodDTO();
        foodDTO.setId(1);
        foodDTO.setName("Pizza");
        foodDTO.setPrice(100.0);

        mockFile = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());
    }

    // Test listFoods
    @Test
    void listFoods_ValidParams_ReturnsFoodPage() {
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String direction = "asc";
        List<FoodDTO> foodList = Arrays.asList(foodDTO);
        Page<FoodDTO> mockPage = new PageImpl<>(foodList, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy)), 1);

        when(foodService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy)))).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<FoodDTO>>> response = foodController.listFoods(page, size, sortBy, direction);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockPage, response.getBody().getData());
        verify(foodService).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy)));
    }

    // Test getFood
    @Test
    void getFood_ValidId_ReturnsFood() {
        int foodId = 1;

        when(foodService.findById(foodId)).thenReturn(foodDTO);

        ResponseEntity<ApiResponse<FoodDTO>> response = foodController.getFood(foodId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(foodDTO, response.getBody().getData());
        verify(foodService).findById(foodId);
    }

    // Test addFood
    @Test
    void addFood_ValidRequest_ReturnsSavedFood() {
        when(foodService.save(food)).thenReturn(foodDTO);

        ResponseEntity<ApiResponse<FoodDTO>> response = foodController.addFood(food);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(foodDTO, response.getBody().getData());
        assertEquals("Food added successfully", response.getBody().getMessage());
        verify(foodService).save(food);
    }

    // Test updateFood
    @Test
    void updateFood_ValidRequest_ReturnsUpdatedFood() {
        int foodId = 1;

        when(foodService.save(food)).thenReturn(foodDTO);

        ResponseEntity<ApiResponse<FoodDTO>> response = foodController.updateFood(foodId, food);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(foodDTO, response.getBody().getData());
        assertEquals("Food updated successfully", response.getBody().getMessage());
        verify(foodService).save(food);
        assertEquals(foodId, food.getId());
    }

    // Test deleteFood
    @Test
    void deleteFood_ValidId_ReturnsSuccess() {
        int foodId = 1;

        doNothing().when(foodService).deleteById(foodId);

        ResponseEntity<ApiResponse<Void>> response = foodController.deleteFood(foodId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Food deleted successfully", response.getBody().getMessage());
        verify(foodService).deleteById(foodId);
    }

    // Test searchFoods
    @Test
    void searchFoods_ValidParams_ReturnsFoodPage() {
        String name = "Pizza";
        Integer categoryId = 1;
        int page = 0;
        int size = 10;
        List<FoodDTO> foodList = Arrays.asList(foodDTO);
        Page<FoodDTO> mockPage = new PageImpl<>(foodList, PageRequest.of(page, size), 1);

        when(foodService.search(name, categoryId, PageRequest.of(page, size))).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<FoodDTO>>> response = foodController.searchFoods(name, categoryId, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockPage, response.getBody().getData());
        verify(foodService).search(name, categoryId, PageRequest.of(page, size));
    }

    // Test uploadFoodImage
    @Test
    void uploadFoodImage_ValidFile_ReturnsImageFilename() {
        int foodId = 1;
        String imageFilename = "test.jpg";

        when(foodService.uploadImage(foodId, mockFile)).thenReturn(imageFilename);

        ResponseEntity<ApiResponse<String>> response = foodController.uploadFoodImage(foodId, mockFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(imageFilename, response.getBody().getData());
        assertEquals("Image uploaded successfully", response.getBody().getMessage());
        verify(foodService).uploadImage(foodId, mockFile);
    }

    // Test getImage
    @Test
    void getImage_ValidFilename_ReturnsImageBytes() throws IOException {
        String filename = "test.jpg";
        byte[] imageBytes = "test image".getBytes();
        Path filePath = Paths.get("src/main/resources/static/images").resolve(filename);

        try (MockedStatic<Files> filesMocked = mockStatic(Files.class)) {
            filesMocked.when(() -> Files.readAllBytes(filePath)).thenReturn(imageBytes);

            ResponseEntity<byte[]> response = foodController.getImage(filename);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertArrayEquals(imageBytes, response.getBody());
            assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
            filesMocked.verify(() -> Files.readAllBytes(filePath));
        }
    }

    // Test getImage with IOException
    @Test
    void getImage_FileNotFound_ReturnsNotFound() throws IOException {
        String filename = "test.jpg";
        Path filePath = Paths.get("src/main/resources/static/images").resolve(filename);

        try (MockedStatic<Files> filesMocked = mockStatic(Files.class)) {
            filesMocked.when(() -> Files.readAllBytes(filePath)).thenThrow(IOException.class);

            ResponseEntity<byte[]> response = foodController.getImage(filename);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            filesMocked.verify(() -> Files.readAllBytes(filePath));
        }
    }
}