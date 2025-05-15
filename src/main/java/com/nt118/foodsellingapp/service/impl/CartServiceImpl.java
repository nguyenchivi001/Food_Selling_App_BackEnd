package com.nt118.foodsellingapp.service.impl;

import com.nt118.foodsellingapp.dto.CartItemDTO;
import com.nt118.foodsellingapp.entity.CartItem;
import com.nt118.foodsellingapp.entity.Food;
import com.nt118.foodsellingapp.entity.User;
import com.nt118.foodsellingapp.exception.ResourceNotFoundException;
import com.nt118.foodsellingapp.mapper.CartItemMapper;
import com.nt118.foodsellingapp.repository.CartItemRepository;
import com.nt118.foodsellingapp.repository.FoodRepository;
import com.nt118.foodsellingapp.repository.UserRepository;
import com.nt118.foodsellingapp.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CartItemMapper cartItemMapper;

    public CartServiceImpl(CartItemRepository cartItemRepository, UserRepository userRepository,
                           FoodRepository foodRepository, CartItemMapper cartItemMapper) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    @Transactional
    public CartItemDTO addToCart(Integer userId, Integer foodId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + foodId));

        if (quantity <= 0 || quantity > food.getStockQuantity()) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndFoodId(userId, foodId);
        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setFood(food);
            cartItem.setQuantity(quantity);
        }
        cartItem = cartItemRepository.save(cartItem);

        return cartItemMapper.toDTO(cartItem);
    }

    @Override
    @Transactional
    public void removeFromCart(Integer userId, Integer foodId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndFoodId(userId, foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart for user: " + userId + " and food: " + foodId));
        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public CartItemDTO updateCartItemQuantity(Integer userId, Integer foodId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByUserIdAndFoodId(userId, foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart for user: " + userId + " and food: " + foodId));
        Food food = cartItem.getFood();
        if (quantity <= 0 || quantity > food.getStockQuantity()) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        cartItem.setQuantity(quantity);
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toDTO(cartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItems(Integer userId) {
        return cartItemRepository.findByUserId(userId)
                .stream()
                .map(cartItemMapper::toDTO)
                .collect(Collectors.toList());
    }
}