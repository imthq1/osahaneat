package com.example.demo.Controller;

import com.example.demo.Domain.Food;
import com.example.demo.Domain.response.ResultPaginationDTO;
import com.example.demo.Service.FoodService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/food")
public class FoodController {
    private final FoodService foodService;
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping
    public ResponseEntity<Food> addFood(@RequestBody Food food) throws IdInvalidException {
        if(this.foodService.filterFoodByCategory(food)==false)
        {
            throw new IdInvalidException("Food exits on Category");
        }
        this.foodService.addFood(food);
        return ResponseEntity.ok(food);
    }
    @GetMapping
    public Page<Food> getAllFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return foodService.getAllFoods(PageRequest.of(page, size));
    }
    @GetMapping("/getFoodBySeller")
    public ResponseEntity<ResultPaginationDTO> getFoodBySeller(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = SecurityUtil.getCurrentUserLogin().get();
        ResultPaginationDTO result = foodService.getAllFoodsBySeller(email, page, size);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public Food getFoodById(@PathVariable Long id) {
        return foodService.getFoodById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));
    }

    @PutMapping("/{id}")
    public Food updateFood(@PathVariable Long id, @RequestBody Food food) {
        return foodService.updateFood(id, food);
    }

    @DeleteMapping("/{id}")
    public void deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
    }

}
