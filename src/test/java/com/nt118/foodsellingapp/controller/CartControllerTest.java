package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.CartItemDTO;
import com.nt118.foodsellingapp.dto.request.CartItemRequest;
import com.nt118.foodsellingapp.dto.response.ApiResponse;
import com.nt118.foodsellingapp.service.CartService;
import com.nt118.foodsellingapp.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CartController cartController;

    private CartItemRequest cartItemRequest;
    private CartItemDTO cartItemDTO;

    @BeforeEach
    void setUp() {
        cartItemRequest = new CartItemRequest();
        cartItemRequest.setFoodId(1);
        cartItemRequest.setQuantity(2);

        cartItemDTO = new CartItemDTO();
        cartItemDTO.setFoodId(1);
        cartItemDTO.setFoodName("Pizza");
        cartItemDTO.setQuantity(2);
        cartItemDTO.setPrice(100.0);
    }

    // Test getCartItems
    @Test
    void getCartItems_ValidUser_ReturnsCartItems() {
        int userId = 1;
        List<CartItemDTO> mockItems = Arrays.asList(cartItemDTO);

        try (MockedStatic<SecurityUtil> securityUtilMocked = mockStatic(SecurityUtil.class)) {
            securityUtilMocked.when(() -> SecurityUtil.extractUserId(authentication)).thenReturn(userId);
            when(cartService.getCartItems(userId)).thenReturn(mockItems);

            ResponseEntity<ApiResponse<List<CartItemDTO>>> response = cartController.getCartItems(authentication);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(mockItems, response.getBody().getData());
            verify(cartService).getCartItems(userId);
        }
    }

    // Test addToCart
    @Test
    void addToCart_ValidRequest_ReturnsCartItem() {
        int userId = 1;

        try (MockedStatic<SecurityUtil> securityUtilMocked = mockStatic(SecurityUtil.class)) {
            securityUtilMocked.when(() -> SecurityUtil.extractUserId(authentication)).thenReturn(userId);
            when(cartService.addToCart(userId, cartItemRequest.getFoodId(), cartItemRequest.getQuantity()))
                    .thenReturn(cartItemDTO);

            ResponseEntity<ApiResponse<CartItemDTO>> response = cartController.addToCart(cartItemRequest, authentication);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(cartItemDTO, response.getBody().getData());
            verify(cartService).addToCart(userId, 1, 2);
        }
    }

    // Test updateCartItemQuantity
    @Test
    void updateCartItemQuantity_ValidRequest_ReturnsUpdatedItem() {
        int userId = 1;
        int foodId = 10;
        int updatedQuantity = 5;

        CartItemRequest request = new CartItemRequest();
        request.setQuantity(updatedQuantity);

        cartItemDTO.setQuantity(updatedQuantity);

        try (MockedStatic<SecurityUtil> securityUtilMocked = mockStatic(SecurityUtil.class)) {
            securityUtilMocked.when(() -> SecurityUtil.extractUserId(authentication)).thenReturn(userId);
            when(cartService.updateCartItemQuantity(userId, foodId, updatedQuantity)).thenReturn(cartItemDTO);

            ResponseEntity<ApiResponse<CartItemDTO>> response =
                    cartController.updateCartItemQuantity(foodId, request, authentication);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(updatedQuantity, response.getBody().getData().getQuantity());
            verify(cartService).updateCartItemQuantity(userId, foodId, updatedQuantity);
        }
    }

    // Test deleteCartItem
    @Test
    void deleteCartItem_ValidRequest_ReturnsSuccess() {
        int userId = 1;
        int cartItemId = 10;

        try (MockedStatic<SecurityUtil> securityUtilMocked = mockStatic(SecurityUtil.class)) {
            securityUtilMocked.when(() -> SecurityUtil.extractUserId(authentication)).thenReturn(userId);
            doNothing().when(cartService).removeFromCart(userId, cartItemId);

            ResponseEntity<ApiResponse<Void>> response = cartController.removeFromCart(cartItemId, authentication);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Item removed from cart successfully", response.getBody().getMessage());
            verify(cartService).removeFromCart(userId, cartItemId);
        }
    }

    // Test clearCart
    @Test
    void clearCart_ValidRequest_ReturnsSuccess() {
        int userId = 1;

        try (MockedStatic<SecurityUtil> securityUtilMocked = mockStatic(SecurityUtil.class)) {
            securityUtilMocked.when(() -> SecurityUtil.extractUserId(authentication)).thenReturn(userId);
            doNothing().when(cartService).clearCart(userId);

            ResponseEntity<ApiResponse<Void>> response = cartController.clearCart(authentication);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Cart cleared successfully", response.getBody().getMessage());
            verify(cartService).clearCart(userId);
        }
    }
}
