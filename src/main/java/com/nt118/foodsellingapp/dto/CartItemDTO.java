package com.nt118.foodsellingapp.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Integer id;
    private Integer foodId;
    private String foodName;
    private Integer quantity;
    private Double price;
    private String imageFilename;
    private String createdAt;
}
