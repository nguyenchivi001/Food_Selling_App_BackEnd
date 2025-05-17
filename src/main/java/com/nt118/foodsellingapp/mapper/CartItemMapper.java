package com.nt118.foodsellingapp.mapper;

import com.nt118.foodsellingapp.dto.CartItemDTO;
import com.nt118.foodsellingapp.entity.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper {
    public CartItemDTO toDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(cartItem.getId());
        cartItemDTO.setFoodId(cartItem.getFood().getId());
        cartItemDTO.setFoodName(cartItem.getFood().getName());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        cartItemDTO.setPrice(cartItem.getFood().getPrice());
        cartItemDTO.setImageFilename(cartItem.getFood().getImageFilename());
        cartItemDTO.setCreatedAt(cartItem.getCreatedAt().toString());
        return cartItemDTO;
    }
}
