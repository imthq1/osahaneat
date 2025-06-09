package com.example.demo.Domain.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private List<FoodOrder> foods;

    @Getter
    @Setter
    public static class FoodOrder {
        private Long foodId;
        private int quantity;
    }
}

