package com.nt118.foodsellingapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="foods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="price")
    private int price;

    @Column(name="image_url")
    private String imageUrl;

    @Column(name="category_id")
    private int categoryId;
}


