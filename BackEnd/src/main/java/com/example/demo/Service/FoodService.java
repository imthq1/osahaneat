package com.example.demo.Service;

import com.example.demo.Domain.Category;
import com.example.demo.Domain.Food;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;
    public FoodService(FoodRepository foodRepository, CategoryRepository categoryRepository) {
        this.foodRepository = foodRepository;
        this.categoryRepository = categoryRepository;
    }
    public Food addFood(Food food) {
        Optional<Category> categoryOptional = categoryRepository.findById(food.getCategory().getId());
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            food.setCategory(category);
        }
        return foodRepository.save(food);
    }
    public Boolean filterFoodByCategory(Food food) {
        if (food.getCategory() == null) {
            return false;
        }

        Optional<Category> categoryOptional = categoryRepository.findById(food.getCategory().getId());
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            boolean isFoodExist = category.getFoods().stream()
                    .anyMatch(existingFood -> existingFood.getName().equals(food.getName()));
            if (isFoodExist) {
                return false;
            }
        }

        return true;
    }


}
