package com.example.demo.Domain;

import com.example.demo.util.constant.Status;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "restaurants")
public class Restaurant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String address;

    private String description;

    private int rating;

    private String logo;

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<Image> imageList = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    @JsonManagedReference("restaurant-food")
    private List<Food> foods;

    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<RatingRestaurant> ratingRestaurants;

    @OneToOne
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("restaurant-category")
    private List<Category> categories;




    @PrePersist
    public void setDefaultStatus() {
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }
}
