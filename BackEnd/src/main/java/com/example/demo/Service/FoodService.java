package com.example.demo.Service;

import com.example.demo.Domain.Category;
import com.example.demo.Domain.Food;
import com.example.demo.Domain.Restaurant;
import com.example.demo.Domain.User;
import com.example.demo.Domain.response.ResultPaginationDTO;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.FoodRepository;
import com.example.demo.Repository.RestaurantRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public FoodService(FoodRepository foodRepository, CategoryRepository categoryRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.foodRepository = foodRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }
    public Food addFood(Food food) {
        Restaurant restaurant=this.restaurantRepository.findByUser(userRepository.findByEmail(SecurityUtil.getCurrentUserLogin().get()));
        Optional<Category> categoryOptional = categoryRepository.findById(food.getCategory().getId());
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            food.setCategory(category);
        }
        food.setRestaurant(restaurant);
        return foodRepository.save(food);
    }
    public ResultPaginationDTO getAllFoodsBySeller(String email, int page, int size) {
        User user = userRepository.findByEmail(email);
        Restaurant restaurant = restaurantRepository.findByUser(user);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Food> foodPage = foodRepository.findByRestaurant_Id(restaurant.getId(), pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();

        // Meta
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page);
        meta.setPageSize(size);
        meta.setPages(foodPage.getTotalPages());
        meta.setTotal(foodPage.getTotalElements());

        result.setMeta(meta);
        result.setResult(foodPage.getContent());

        return result;
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
    public Page<Food> getAllFoods(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }


    public Optional<Food> getFoodById(Long id) {
        return foodRepository.findById(id);
    }


    public Food createFood(Food food) {
        return foodRepository.save(food);
    }


    public Food updateFood(Long id, Food food) {
        Food existing = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));
        existing.setName(food.getName());
        existing.setDescription(food.getDescription());
        existing.setPrice(food.getPrice());
        existing.setQuantity(food.getQuantity());
        existing.setIntruction(food.getIntruction());
        existing.setCategory(food.getCategory());
        return foodRepository.save(existing);
    }

    public void deleteFood(Long id) {
        foodRepository.deleteById(id);
    }

}
