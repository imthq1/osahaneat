package com.example.demo.Domain;

import com.example.demo.util.constant.StatusOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double totalPrice;
    private Instant createDate;

    @Enumerated(EnumType.STRING)
    private StatusOrder status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;

    @ManyToMany
    @JoinTable(
            name = "order_food",
            joinColumns = @JoinColumn(name = "food_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private List<Food> foods;


    @PrePersist
    protected void onCreate() {
        createDate = Instant.now();
    }

}
