package com.example.demo.Controller;

import com.example.demo.Domain.Category;
import com.example.demo.Domain.User;
import com.example.demo.Domain.response.CategoryDTO;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.RestaurantService;
import com.example.demo.Service.UserService;
import com.example.demo.util.ApiMessage;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.error.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

        public CategoryController(CategoryService categoryService, UserService userService ) {
    this.categoryService = categoryService;
    this.userService = userService;
}
    @PostMapping("/categories")
    @ApiMessage("Create a new Category")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody Category category) throws IdInvalidException {
        String email= SecurityUtil.getCurrentUserLogin().get();
        User user = userService.findByEmail(email);

        if(user.getRestaurant()==null)
        {
            throw  new IdInvalidException("User haven't Restaurant");
        }


            return ResponseEntity.ok().body(this.categoryService.createCate(category,user.getRestaurant()));
    }
    @GetMapping("/categories")
    @ApiMessage("Get All category by Seller")
    public ResponseEntity<List<Category>> getAllCategories() throws IdInvalidException {
            return ResponseEntity.ok(this.categoryService.getCategoriesByRestaurant(SecurityUtil.getCurrentUserLogin().get()));
    }

}
