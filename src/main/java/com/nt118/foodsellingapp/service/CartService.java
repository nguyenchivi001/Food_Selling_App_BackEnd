package com.nt118.foodsellingapp.service;

import com.nt118.foodsellingapp.dto.CartItemDTO;

import java.util.List;

public interface CartService {
    CartItemDTO addToCart(Integer userId, Integer foodId, Integer quantity);
    void removeFromCart(Integer userId, Integer foodId);
    CartItemDTO updateCartItemQuantity(Integer userId, Integer foodId, Integer quantity);
    List<CartItemDTO> getCartItems(Integer userId);
    void clearCart(int userId);
}
