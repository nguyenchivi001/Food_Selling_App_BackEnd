package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.Response.ApiResponse;
import com.nt118.foodsellingapp.dto.CartItemDTO;
import com.nt118.foodsellingapp.dto.request.CartItemRequest;
import com.nt118.foodsellingapp.service.CartService;
import com.nt118.foodsellingapp.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> getCartItems(Authentication authentication) {
        int userId = SecurityUtil.extractUserId(authentication);
        List<CartItemDTO> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(ApiResponse.success(cartItems));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemDTO>> addToCart(
            @Valid @RequestBody CartItemRequest request,
            Authentication authentication) {
        int userId = SecurityUtil.extractUserId(authentication);
        CartItemDTO cartItem = cartService.addToCart(userId, request.getFoodId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cartItem, "Item added to cart successfully"));
    }

    @PutMapping("/{foodId}")
    public ResponseEntity<ApiResponse<CartItemDTO>> updateCartItemQuantity(
            @PathVariable Integer foodId,
            @Valid @RequestBody CartItemRequest request,
            Authentication authentication) {
        int userId = SecurityUtil.extractUserId(authentication);
        CartItemDTO updatedItem = cartService.updateCartItemQuantity(userId, foodId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success(updatedItem, "Cart item quantity updated successfully"));
    }

    @DeleteMapping("/{foodId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable Integer foodId,
            Authentication authentication) {
        int userId = SecurityUtil.extractUserId(authentication);
        cartService.removeFromCart(userId, foodId);
        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from cart successfully"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        int userId = SecurityUtil.extractUserId(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared successfully"));
    }
}
