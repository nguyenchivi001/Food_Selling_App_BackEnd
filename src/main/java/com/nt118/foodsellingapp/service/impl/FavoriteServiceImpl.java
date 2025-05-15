package com.nt118.foodsellingapp.service.impl;

import com.nt118.foodsellingapp.dto.FavoriteItemDTO;
import com.nt118.foodsellingapp.entity.FavoriteItem;
import com.nt118.foodsellingapp.entity.Food;
import com.nt118.foodsellingapp.entity.User;
import com.nt118.foodsellingapp.exception.ResourceNotFoundException;
import com.nt118.foodsellingapp.mapper.FavoriteItemMapper;
import com.nt118.foodsellingapp.repository.FavoriteItemRepository;
import com.nt118.foodsellingapp.repository.FoodRepository;
import com.nt118.foodsellingapp.repository.UserRepository;
import com.nt118.foodsellingapp.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteItemRepository favoriteItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private FavoriteItemMapper favoriteItemMapper;

    public FavoriteServiceImpl(FavoriteItemRepository favoriteItemRepository, UserRepository userRepository,
                               FoodRepository foodRepository, FavoriteItemMapper favoriteItemMapper) {
        this.favoriteItemRepository = favoriteItemRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.favoriteItemMapper = favoriteItemMapper;
    }

    @Override
    @Transactional
    public FavoriteItemDTO addToFavorites(Integer userId, Integer foodId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + foodId));

        Optional<FavoriteItem> existingFavorite = favoriteItemRepository.findByUserIdAndFoodId(userId, foodId);
        if (existingFavorite.isPresent()) {
            return favoriteItemMapper.toDTO(existingFavorite.get());
        }

        FavoriteItem favoriteItem = new FavoriteItem();
        favoriteItem.setUser(user);
        favoriteItem.setFood(food);
        favoriteItem = favoriteItemRepository.save(favoriteItem);
        return favoriteItemMapper.toDTO(favoriteItem);
    }

    @Override
    @Transactional
    public void removeFromFavorites(Integer userId, Integer foodId) {
        FavoriteItem favoriteItem = favoriteItemRepository.findByUserIdAndFoodId(userId, foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite item not found for user: " + userId + " and food: " + foodId));
        favoriteItemRepository.delete(favoriteItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteItemDTO> getFavoriteItems(Integer userId) {
        return favoriteItemRepository.findByUserId(userId)
                .stream()
                .map(favoriteItemMapper::toDTO)
                .collect(Collectors.toList());
    }
}