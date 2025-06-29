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

import java.util.ArrayList;
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
    public List<Category> getCategoriesByRestaurant(String email) {
        User user = userRepository.findByEmail(email);

        return this.categoryRepository.findByRestaurantId(user.getRestaurant().getId());
    }
    public Category findByCategoryName(String categoryName) {
        return this.categoryRepository.findByName(categoryName);
    }

    public Category findById(Long id) {
        return this.categoryRepository.findById(id).orElse(null);
    }
        public CategoryDTO createCate(Category category,Restaurant restaurant ) {
            category.setRestaurant(restaurant);
            this.categoryRepository.save(category);

            CategoryDTO categoryDTO = new CategoryDTO();

            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            categoryDTO.setId(category.getId());

            return categoryDTO;
        }
        public List<CategoryDTO> getAllCategories() {
        List<Category> categories=this.categoryRepository.findAll();
        List<CategoryDTO> categoryDTOS=new ArrayList<>();
        for(Category category:categories)
        {
           CategoryDTO categoryDTO=new CategoryDTO();
           categoryDTO.setName(category.getName());
           categoryDTO.setDescription(category.getDescription());
           categoryDTO.setId(category.getId());
           categoryDTOS.add(categoryDTO);
        }
        return categoryDTOS;
        }

}
