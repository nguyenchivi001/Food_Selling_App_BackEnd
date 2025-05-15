package com.nt118.foodsellingapp.repository;


import com.nt118.foodsellingapp.entity.FavoriteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Integer> {
    List<FavoriteItem> findByUserId(Integer userId);
    Optional<FavoriteItem> findByUserIdAndFoodId(Integer userId, Integer foodId);
}
