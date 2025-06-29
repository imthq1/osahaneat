package com.example.demo.Controller;

import com.example.demo.Domain.Restaurant;
import com.example.demo.Domain.response.FileInfo;
import com.example.demo.Domain.response.RestaurantDTO;
import com.example.demo.Service.ImageService;
import com.example.demo.Service.RestaurantService;
import com.example.demo.Service.UserService;
import com.example.demo.config.RabbitMQ.JobQueue;
import com.example.demo.config.cloundinary.CloudinaryService;
import com.example.demo.util.ApiMessage;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RestaurantController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final RestaurantService restaurantService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final ImageService imageService;
    public RestaurantController(RestaurantService restaurantService, CloudinaryService cloudinaryService, UserService userService, ImageService imageService) {
        this.restaurantService = restaurantService;
        this.cloudinaryService = cloudinaryService;
        this.userService = userService;
        this.imageService = imageService;
    }
    @PostMapping("/restaurant/load/{id}")
    @ApiMessage("Upload file Restaurant")
    public ResponseEntity<?> load(@PathVariable long id,
                                  @RequestParam("folder") String folderName) throws IdInvalidException {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        if (restaurant.getLogo() == null) {
            throw new IdInvalidException("File not fould");
        }

        Map<String, Object> cloudinaryFile = cloudinaryService.getFileInfo(restaurant.getLogo(), folderName);
        if (cloudinaryFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found on Cloudinary");
        }

        FileInfo fileInfo=new FileInfo();
        String secureUrl = (String) cloudinaryFile.get("secure_url");

        fileInfo.setName("restaurant");
        fileInfo.setUrl(secureUrl);

        return ResponseEntity.ok(fileInfo);
    }

    @PostMapping("/restaurants")
    @ApiMessage("Create a new Restaurant")
    public ResponseEntity<RestaurantDTO> add(@RequestBody Restaurant restaurant) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().get();
        if (this.restaurantService.findByUser(this.userService.findByEmail(email)) != null) {
            throw new IdInvalidException("User already exists");
        }
        if (this.restaurantService.findByName(restaurant.getName()) != null) {
            throw new IdInvalidException("Restaurant already exists");
        }
        return ResponseEntity.ok().body(this.restaurantService.createRestaurant(restaurant));
    }


    @PutMapping("/restaurant")
    @ApiMessage("Update a Restaurant")
    public ResponseEntity<?> update(@RequestBody Restaurant restaurant) throws IdInvalidException {
        Restaurant resDB=this.restaurantService.getRestaurantById(restaurant.getId());
        if(resDB == null) {
            throw new IdInvalidException("Restaurant does not exist");
        }
        return ResponseEntity.ok().body(this.restaurantService.update(restaurant,resDB));
    }
    @GetMapping("/restaurants")
    @ApiMessage("Get all Restaurant")
    public ResponseEntity<?> getAll(@Filter Specification<Restaurant> specification, Pageable pageable) {
        return ResponseEntity.ok().body(this.restaurantService.fillAll(specification, pageable));
    }
    @PutMapping("/restaurants/accept/{id}")
    @ApiMessage("Accept for Restaurant")
    public ResponseEntity<Restaurant> Accept(@PathVariable Long id ) throws IdInvalidException {
    if(this.restaurantService.findById(id)==null)
    {
        throw new IdInvalidException("Restaurant not exists");
    }
    return ResponseEntity.ok().body(this.restaurantService.Accept(id));
    }
    @GetMapping("/restaurants/pending")
    @ApiMessage("Get All Restaurant")
    public ResponseEntity<?> getAllPending(Pageable pageable) {
        return ResponseEntity.ok().body(this.restaurantService.findPendingRestaurants(pageable));
    }
    @GetMapping("/restaurants/approved")
    @ApiMessage("Get All Restaurant")
    public ResponseEntity<?> getAllApproved(@Filter  Specification<Restaurant> specification, Pageable pageable) {
        return ResponseEntity.ok().body(this.restaurantService.findAllApproved(specification,pageable));
    }

    @DeleteMapping("/restaurants/{id}")
    @ApiMessage("Delete a Restaurant")
    public ResponseEntity<?> delete(@PathVariable Long id) throws IdInvalidException {
        if(this.restaurantService.getRestaurantById(id)==null)
        {
            throw new IdInvalidException("Restaurant does not exist");
        }
        this.restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok().build();
    }

    @ApiMessage("Test RabbitMQ")
    @PostMapping("/restaurants/subscriber")
    public ResponseEntity<Restaurant> subscribe(@RequestBody Restaurant restaurant) throws IdInvalidException {
        if (this.restaurantService.findByName(restaurant.getName()) != null) {
            throw new IdInvalidException("Restaurant already exists");
        }
        this.restaurantService.save(restaurant);


        try {
            rabbitTemplate.convertAndSend(JobQueue.QUEUE_PROCESS, restaurant.getName());
            System.out.println("Message sent to queue: " + restaurant.getName());
        } catch (Exception e) {
            System.err.println("Failed to send message to queue: " + e.getMessage());
        }

        return ResponseEntity.ok(restaurant);
    }
    @ApiMessage("Get Restaurant By Id")
    @GetMapping("/restaurants/{id}")
    public ResponseEntity<Restaurant> getRestaurant(@PathVariable Long id) throws IdInvalidException {
        Restaurant restaurant=this.restaurantService.findById(id);
        return ResponseEntity.ok(restaurant);
    }

}
