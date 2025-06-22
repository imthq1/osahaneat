package com.example.demo.Domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String logo;
    private String description;
    private double price;
    private long quantity;
    private String intruction;

    @ManyToMany(mappedBy = "foods", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders;

    @ManyToOne
    @JoinColumn(name = "cate_id")
    @JsonBackReference("category-food")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference("restaurant-food")
    private Restaurant restaurant;
}
