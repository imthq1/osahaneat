package com.example.demo.Domain;

import com.example.demo.util.constant.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonManagedReference
    private List<Image> imageList = new ArrayList<>();


    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<RatingRestaurant> ratingRestaurants;

    @OneToOne
    @JsonIgnore
    private User user;

    @ManyToMany(mappedBy = "restaurants", fetch = FetchType.LAZY)
    @JsonProperty("categories")
    private List<Category> categories;



    @PrePersist
    public void setDefaultStatus() {
        if (this.status == null) {
            this.status = Status.PENDING;
        }
    }
}
