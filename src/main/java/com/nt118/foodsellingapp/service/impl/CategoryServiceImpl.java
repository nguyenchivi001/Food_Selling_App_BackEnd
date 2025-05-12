package com.nt118.foodsellingapp.service.impl;

import com.nt118.foodsellingapp.dto.CategoryDTO;
import com.nt118.foodsellingapp.mapper.CategoryMapper;
import com.nt118.foodsellingapp.repository.CategoryRepository;
import com.nt118.foodsellingapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<CategoryDTO> categories = categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());

        return categories;
    }

}