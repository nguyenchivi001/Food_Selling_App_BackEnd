package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.CategoryDTO;
import com.nt118.foodsellingapp.dto.response.ApiResponse;
import com.nt118.foodsellingapp.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1);
        categoryDTO.setName("Beverages");
    }

    @Test
    void listCategories_ReturnsCategories() {
        List<CategoryDTO> mockCategories = Arrays.asList(categoryDTO);
        when(categoryService.getAllCategories()).thenReturn(mockCategories);

        ResponseEntity<ApiResponse<List<CategoryDTO>>> response = categoryController.listCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCategories, response.getBody().getData());
        verify(categoryService, times(1)).getAllCategories();
    }
}