package com.example.demo.Domain;

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
public class Oder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double totalPrice;

    private Instant createDate;


    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;

    @ManyToMany
    @JsonIgnoreProperties(value = {"orders"})
    @JoinTable(name = "order_food",joinColumns = @JoinColumn(name = "order_id"),inverseJoinColumns = @JoinColumn(name = "food_id"))
    private List<Food> foods;


    @PrePersist
    protected void onCreate() {
        createDate = Instant.now();
    }

}
