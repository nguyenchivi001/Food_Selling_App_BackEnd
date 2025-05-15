package com.nt118.foodsellingapp.dto.request;

import jakarta.validation.constraints.NotNull;

public class FavoriteItemRequest {

    @NotNull(message = "Food ID must not be null")
    private Integer foodId;

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }
}
