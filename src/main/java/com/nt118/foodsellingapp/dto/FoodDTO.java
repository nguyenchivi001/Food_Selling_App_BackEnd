package com.nt118.foodsellingapp.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodDTO {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageFilename;
    private int categoryId;
    private String categoryName;
    private Integer stockQuantity;
    private boolean available;
} 