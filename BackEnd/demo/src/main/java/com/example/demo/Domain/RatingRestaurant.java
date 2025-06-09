package com.example.demo.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "rating_restaurant")
@Getter
@Setter
public class RatingRestaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float star;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "res_id", referencedColumnName = "id")
    @JsonIgnoreProperties
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

}
