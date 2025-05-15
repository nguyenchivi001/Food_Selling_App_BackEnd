package com.nt118.foodsellingapp.service;

import com.nt118.foodsellingapp.dto.FavoriteItemDTO;

import java.util.List;

public interface FavoriteService {
    FavoriteItemDTO addToFavorites(Integer userId, Integer foodId);
    void removeFromFavorites(Integer userId, Integer foodId);
    List<FavoriteItemDTO> getFavoriteItems(Integer userId);
}