package com.nt118.foodsellingapp.service;

import com.nt118.foodsellingapp.dao.FoodRepository;
import com.nt118.foodsellingapp.entity.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodServiceImpl implements FoodService {
    private FoodRepository foodRepository;

    @Autowired
    public FoodServiceImpl(FoodRepository theFoodRepository) {
        foodRepository = theFoodRepository;
    }

    @Override
    public List<Food> findAll() {
        return foodRepository.findAll();
    }

    @Override
    public Food findById(int theId) {
        Optional<Food> result = foodRepository.findById(theId);

        Food theFood = null;

        if(result.isPresent()) {
            theFood = result.get();
        } else {
            throw new RuntimeException("Dia not find food id - " + theId);
        }
        return theFood;
    }

    @Override
    public Food save(Food theFood) {
        return foodRepository.save(theFood);
    }

    @Override
    public void deleteById(int theId) {
        foodRepository.deleteById(theId);
    }
}
