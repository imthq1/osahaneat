package com.example.demo.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Res")
    @JsonIgnoreProperties(value = {"categories"})
    private Restaurant restaurant;


    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Food> foods;


}
