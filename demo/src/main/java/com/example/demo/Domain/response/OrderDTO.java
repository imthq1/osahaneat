package com.example.demo.Domain.response;

import com.example.demo.util.constant.StatusOrder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private long id;
    private double totalPrice;
    private Instant createDate;
    private List<Food> foodList;

    @Enumerated(EnumType.STRING)
    private StatusOrder status;

    @Getter
    @Setter
    public static class Food{
        private String name;
        private double price;
        private long quantity;
    }
}
