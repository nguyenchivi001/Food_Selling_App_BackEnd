package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.FavoriteItemDTO;
import com.nt118.foodsellingapp.dto.request.FavoriteItemRequest;
import com.nt118.foodsellingapp.dto.response.ApiResponse;
import com.nt118.foodsellingapp.service.FavoriteService;
import com.nt118.foodsellingapp.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FavoriteController favoriteController;

    private FavoriteItemRequest favoriteItemRequest;
    private FavoriteItemDTO favoriteItemDTO;

    @BeforeEach
    void setUp() {
        favoriteItemRequest = new FavoriteItemRequest();
        favoriteItemRequest.setFoodId(1);

        favoriteItemDTO = new FavoriteItemDTO();
        favoriteItemDTO.setFoodId(1);
        favoriteItemDTO.setFoodName("Burger");
        favoriteItemDTO.setPrice(50.0);
    }

    // Test getFavoriteItems
    @Test
    void getFavoriteItems_ValidUser_ReturnsFavoriteItems() {
        int userId = 1;
        List<FavoriteItemDTO> mockItems = Arrays.asList(favoriteItemDTO);

        try (MockedStatic<SecurityUtil> securityUtilMocked = mockStatic(SecurityUtil.class)) {
            securityUtilMocked.when(() -> SecurityUtil.extractUserId(authentication)).thenReturn(userId);
            when(favoriteService.getFavoriteItems(userId)).thenReturn(mockItems);

            ResponseEntity<ApiResponse<List<FavoriteItemDTO>>> response = favoriteController.getFavoriteItems(authentication);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(mockItems, response.getBody().getData());
            verify(favoriteService).getFavoriteItems(userId);
        }
    }

    // Test addToFavorites
    @Test
    void addToFavorites_ValidRequest_ReturnsFavoriteItem() {
        int userId = 1;

        try (MockedStatic<SecurityUtil> securityUtilMocked = mockStatic(SecurityUtil.class)) {
            securityUtilMocked.when(() -> SecurityUtil.extractUserId(authentication)).thenReturn(userId);
            when(favoriteService.addToFavorites(userId, favoriteItemRequest.getFoodId())).thenReturn(favoriteItemDTO);

            ResponseEntity<ApiResponse<FavoriteItemDTO>> response =
                    favoriteController.addToFavorites(favoriteItemRequest, authentication);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(favoriteItemDTO, response.getBody().getData());
            assertEquals("Item added to favorites successfully", response.getBody().getMessage());
            verify(favoriteService).addToFavorites(userId, favoriteItemRequest.getFoodId());
        }
    }

    // Test removeFromFavorites
    @Test
    void removeFromFavorites_ValidRequest_ReturnsSuccess() {
        int userId = 1;
        int foodId = 1;

        try (MockedStatic<SecurityUtil> securityUtilMocked = mockStatic(SecurityUtil.class)) {
            securityUtilMocked.when(() -> SecurityUtil.extractUserId(authentication)).thenReturn(userId);
            doNothing().when(favoriteService).removeFromFavorites(userId, foodId);

            ResponseEntity<ApiResponse<Void>> response = favoriteController.removeFromFavorites(foodId, authentication);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Item removed from favorites successfully", response.getBody().getMessage());
            verify(favoriteService).removeFromFavorites(userId, foodId);
        }
    }
}