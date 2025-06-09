package com.example.demo.Controller;

import com.example.demo.Domain.RatingRestaurant;
import com.example.demo.Domain.Restaurant;
import com.example.demo.Domain.User;
import com.example.demo.Service.RatingRestaurantService;
import com.example.demo.Service.RestaurantService;
import com.example.demo.Service.UserService;
import com.example.demo.util.ApiMessage;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.error.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RatingRestaurantController {
    private final RatingRestaurantService ratingRestaurantService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    public RatingRestaurantController(RatingRestaurantService ratingRestaurantService, UserService userService, RestaurantService restaurantService) {
        this.ratingRestaurantService = ratingRestaurantService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }
    @PostMapping("/ratingRestaurants")
    @ApiMessage("Create a new Rating Restaurant")
    public ResponseEntity<RatingRestaurant> createRatingRestaurant(@RequestBody RatingRestaurant ratingRestaurant) throws IdInvalidException {

        Restaurant restaurant = this.restaurantService.findByName(ratingRestaurant.getRestaurant().getName());
        if (restaurant == null) {
            throw new IdInvalidException("Restaurant Name Not Found");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userService.findByEmail(username);

        ratingRestaurant.setUser(user);
        ratingRestaurant.setRestaurant(restaurant);


        RatingRestaurant savedRating = ratingRestaurantService.createRatingRestaurant(ratingRestaurant);
        return ResponseEntity.ok(savedRating);
    }


}
