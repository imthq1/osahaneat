package com.example.demo.Service;

import com.example.demo.Domain.RatingRestaurant;
import com.example.demo.Repository.RatingRestaurantRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingRestaurantService {
    private final RatingRestaurantRepository ratingRestaurantRepository;
    public RatingRestaurantService(RatingRestaurantRepository ratingRestaurantRepository) {
        this.ratingRestaurantRepository = ratingRestaurantRepository;
    }
    public RatingRestaurant createRatingRestaurant(RatingRestaurant ratingRestaurant) {
        return ratingRestaurantRepository.save(ratingRestaurant);
    }
}
