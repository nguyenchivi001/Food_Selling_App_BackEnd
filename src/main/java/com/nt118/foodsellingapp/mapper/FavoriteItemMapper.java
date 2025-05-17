package com.nt118.foodsellingapp.mapper;

import com.nt118.foodsellingapp.dto.FavoriteItemDTO;
import com.nt118.foodsellingapp.entity.FavoriteItem;
import org.springframework.stereotype.Component;

@Component
public class FavoriteItemMapper {
    public FavoriteItemDTO toDTO(FavoriteItem favoriteItem) {
        FavoriteItemDTO favoriteItemDTO = new FavoriteItemDTO();
        favoriteItemDTO.setId(favoriteItem.getId());
        favoriteItemDTO.setFoodId(favoriteItem.getFood().getId());
        favoriteItemDTO.setFoodName(favoriteItem.getFood().getName());
        favoriteItemDTO.setPrice(favoriteItem.getFood().getPrice());
        favoriteItemDTO.setImageFilename(favoriteItem.getFood().getImageFilename());
        favoriteItemDTO.setCreatedAt(favoriteItem.getCreatedAt().toString());
        return favoriteItemDTO;
    }
}