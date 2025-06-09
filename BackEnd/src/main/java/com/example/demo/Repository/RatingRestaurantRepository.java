package com.example.demo.Repository;


import com.example.demo.Domain.RatingRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRestaurantRepository extends JpaRepository<RatingRestaurant, Long> {
    RatingRestaurant save(RatingRestaurant ratingRestaurant);
    List<RatingRestaurant> findByIdIn(List<Long> restaurantId);
}
