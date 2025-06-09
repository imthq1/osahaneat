package com.example.demo.Domain;

import com.example.demo.util.SecurityUtil;
import com.example.demo.util.constant.AuthProvider;
import com.example.demo.util.constant.Enable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;


    private String password;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    private String fullname;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @Enumerated(EnumType.STRING)
    private Enable enable;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    private String image_url;
    private String address;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "users")
    @JsonIgnore
    private List<Order> oders;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RatingRestaurant> ratingRestaurants;

    @OneToOne(mappedBy = "user")
    private Restaurant restaurant;


    @PrePersist
    public void BeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void BeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get() : "";
    }
}
