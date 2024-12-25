package com.example.demo.Service;

import com.example.demo.Domain.Category;
import com.example.demo.Domain.Restaurant;
import com.example.demo.Domain.User;
import com.example.demo.Domain.response.CategoryDTO;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.RestaurantRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }
    public Category findByCategoryName(String categoryName) {
        return this.categoryRepository.findByName(categoryName);
    }

    public Category findById(Long id) {
        return this.categoryRepository.findById(id).orElse(null);
    }
        public CategoryDTO createCate(Category category) {
            if(category.getRestaurants()!=null)
            {
                List<Long> listId=category.getRestaurants().stream().map(x->x.getId()).collect(Collectors.toList());
                List<Restaurant> restaurants=restaurantRepository.findAllById(listId);
                category.setRestaurants(restaurants);
            }
            this.categoryRepository.save(category);

            CategoryDTO categoryDTO = new CategoryDTO();

            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            categoryDTO.setId(category.getId());

            return categoryDTO;
        }

    public Boolean findByUserAndCategory(String email, String nameCate) {
        User user=this.userRepository.findByEmail(email);
        Restaurant restaurant=this.restaurantRepository.findByUser(user);
        if(this.categoryRepository.existsByNameAndRestaurants_Name(nameCate,restaurant.getName())==true)
        {
            return false;
        }

        return true;
    }

}
