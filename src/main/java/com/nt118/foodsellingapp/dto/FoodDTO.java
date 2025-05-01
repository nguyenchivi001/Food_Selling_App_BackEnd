package com.nt118.foodsellingapp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
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