package com.nt118.foodsellingapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodDTO {
    private int id;
    private String name;
    private String description;
    private Double price;
    private String imageFilename;
    private int categoryId;
    private String categoryName;
    private Integer stockQuantity;
    private boolean available;
} 