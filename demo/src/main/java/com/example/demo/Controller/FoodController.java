package com.example.demo.Controller;

import com.example.demo.Domain.Food;
import com.example.demo.Service.FoodService;
import com.example.demo.util.error.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class FoodController {
    private final FoodService foodService;
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping("/foods")
    public ResponseEntity<Food> addFood(@RequestBody Food food) throws IdInvalidException {
        if(this.foodService.filterFoodByCategory(food)==false)
        {
            throw new IdInvalidException("Food exits on Category");
        }
        this.foodService.addFood(food);
        return ResponseEntity.ok(food);
    }


}
