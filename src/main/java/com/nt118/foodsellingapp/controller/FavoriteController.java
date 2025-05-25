package com.nt118.foodsellingapp.controller;

import com.nt118.foodsellingapp.dto.response.ApiResponse;
import com.nt118.foodsellingapp.dto.FavoriteItemDTO;
import com.nt118.foodsellingapp.dto.request.FavoriteItemRequest;
import com.nt118.foodsellingapp.service.FavoriteService;
import com.nt118.foodsellingapp.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-items")
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FavoriteItemDTO>>> getFavoriteItems(Authentication authentication) {
        int userId = SecurityUtil.extractUserId(authentication);
        List<FavoriteItemDTO> favoriteItems = favoriteService.getFavoriteItems(userId);
        return ResponseEntity.ok(ApiResponse.success(favoriteItems));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteItemDTO>> addToFavorites(
            @Valid @RequestBody FavoriteItemRequest request,
            Authentication authentication) {
        int userId = SecurityUtil.extractUserId(authentication);
        FavoriteItemDTO favoriteItem = favoriteService.addToFavorites(userId, request.getFoodId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(favoriteItem, "Item added to favorites successfully"));
    }

    @DeleteMapping("/{foodId}")
    public ResponseEntity<ApiResponse<Void>> removeFromFavorites(
            @PathVariable Integer foodId,
            Authentication authentication) {
        int userId = SecurityUtil.extractUserId(authentication);
        favoriteService.removeFromFavorites(userId, foodId);
        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from favorites successfully"));
    }
}
