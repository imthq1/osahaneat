package com.example.demo.Service;

import com.example.demo.Domain.*;
import com.example.demo.Domain.response.RestaurantDTO;
import com.example.demo.Domain.response.ResultPaginationDTO;
import com.example.demo.Repository.*;
import com.example.demo.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRestaurantRepository ratingRestaurantRepository;
    private final SecurityUtil securityUtil;
    private final ImageService imageService;
    private final UserRepository userRepository;
    public RestaurantService(RestaurantRepository restaurantRepository,
                             CategoryRepository categoryRepository,
                             RatingRestaurantRepository ratingRestaurantRepository,
                             SecurityUtil securityUtil ,
                             UserRepository userRepository
    , ImageService imageService) {
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
        this.ratingRestaurantRepository = ratingRestaurantRepository;
        this.securityUtil = securityUtil;
        this.userRepository = userRepository;
        this.imageService = imageService;
    }
    public Restaurant getRestaurantByLogo(String logo) {
        return restaurantRepository.findByLogo(logo);
    }
    public Restaurant getRestaurantById(long id) {
        return this.restaurantRepository.findById(id);
    }

    @Transactional
    public RestaurantDTO createRestaurant(Restaurant restaurant) {
        RestaurantDTO restaurantDTO = new RestaurantDTO();

        String email = SecurityUtil.getCurrentUserLogin().get();
        User user = this.userRepository.findByEmail(email);
        restaurant.setUser(user);

        Restaurant savedRestaurant = this.restaurantRepository.save(restaurant);

        if (restaurant.getImageList() != null && !restaurant.getImageList().isEmpty()) {
            List<Image> images = restaurant.getImageList().stream().map(img -> {
                Image image = new Image();
                image.setPath(img.getPath());
                image.setRestaurant(savedRestaurant);
                return image;
            }).collect(Collectors.toList());
            this.imageService.saveAll(images);
            savedRestaurant.setImageList(images);
        }

        RestaurantDTO.UserDTO userDTO = new RestaurantDTO.UserDTO();
        userDTO.setId(savedRestaurant.getUser().getId());
        userDTO.setEmail(savedRestaurant.getUser().getEmail());
        userDTO.setFullname(savedRestaurant.getUser().getFullname());
        userDTO.setAddress(savedRestaurant.getUser().getAddress());
        restaurantDTO.setUserDTO(userDTO);

        List<RestaurantDTO.ImageDTO> imageDTOList = savedRestaurant.getImageList().stream().map(img -> {
            RestaurantDTO.ImageDTO imageDTO = new RestaurantDTO.ImageDTO();
            imageDTO.setPath(img.getPath());
            return imageDTO;
        }).collect(Collectors.toList());
        restaurantDTO.setImageDTOS(imageDTOList);

        restaurantDTO.setId(savedRestaurant.getId());
        restaurantDTO.setName(savedRestaurant.getName());
        restaurantDTO.setAddress(savedRestaurant.getAddress());
        restaurantDTO.setDescription(savedRestaurant.getDescription());
        restaurantDTO.setRating(savedRestaurant.getRating());
        restaurantDTO.setLogo(savedRestaurant.getLogo());
        restaurantDTO.setContent(savedRestaurant.getContent());
        restaurantDTO.setStatus(savedRestaurant.getStatus().name());

        return restaurantDTO;
    }


    @Transactional
    public void deleteRestaurant(long id) {
        Optional<Restaurant> optionalRestaurant = Optional.ofNullable(this.restaurantRepository.findById(id));

        if (optionalRestaurant.isPresent()) {
            Restaurant restaurant = optionalRestaurant.get();
            List<Category> categories = restaurant.getCategories();
            for (Category category : categories) {
                categoryRepository.delete(category);
            }

            List<RatingRestaurant> ratings = restaurant.getRatingRestaurants();
            for (RatingRestaurant rating : ratings) {
                ratingRestaurantRepository.delete(rating);
            }

            restaurantRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Restaurant with id " + id + " not found");
        }
    }

    public Restaurant findByUser(User user) {
        return this.restaurantRepository.findByUser(user);
    }
    public Restaurant save(Restaurant restaurant)
    {
        return this.restaurantRepository.save(restaurant);
    }
    public Restaurant findByName(String name) {
        return this.restaurantRepository.findByName(name);
    }
    public ResultPaginationDTO  fillAll(Specification<Restaurant> spec, Pageable pageable) {
        Page<Restaurant> pageRestaurants=this.restaurantRepository.findAll(spec,pageable);
        ResultPaginationDTO res=new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt=new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageRestaurants.getTotalPages());
        mt.setTotal(pageRestaurants.getTotalElements());

        res.setMeta(mt);

        res.setResult(pageRestaurants);

        return res;
    }

    public RestaurantDTO update(Restaurant restaurant, Restaurant resDB) {
        resDB.setAddress(restaurant.getAddress());
        resDB.setName(restaurant.getName());
        resDB.setLogo(restaurant.getLogo());
        resDB.setContent(restaurant.getContent());
        resDB.setDescription(restaurant.getDescription());
        resDB.setStatus(restaurant.getStatus());

        if (restaurant.getCategories() != null) {
            List<Long> idCate = restaurant.getCategories().stream()
                    .map(x -> x.getId()).collect(Collectors.toList());
            List<Category> listCate = this.categoryRepository.findByIdIn(idCate);
            resDB.setCategories(listCate);
        }

        if (restaurant.getRatingRestaurants() != null) {
            List<Long> idRating = restaurant.getRatingRestaurants().stream()
                    .map(x -> x.getId()).collect(Collectors.toList());
            List<RatingRestaurant> listRating = this.ratingRestaurantRepository.findByIdIn(idRating);
            resDB.setRatingRestaurants(listRating);
        }

        this.restaurantRepository.save(resDB);

        RestaurantDTO resDTO = new RestaurantDTO();
        resDTO.setId(resDB.getId());
        resDTO.setName(resDB.getName());
        resDTO.setAddress(resDB.getAddress());
        resDTO.setDescription(resDB.getDescription());
        resDTO.setRating(resDB.getRating());
        resDTO.setLogo(resDB.getLogo());
        resDTO.setContent(resDB.getContent());
        resDTO.setStatus(resDB.getStatus() != null ? resDB.getStatus().toString() : null);

        if (resDB.getUser() != null) {
            RestaurantDTO.UserDTO userDTO = new RestaurantDTO.UserDTO();
            userDTO.setId(resDB.getUser().getId());
            userDTO.setEmail(resDB.getUser().getEmail());
            userDTO.setFullname(resDB.getUser().getFullname());
            userDTO.setAddress(resDB.getUser().getAddress());
            resDTO.setUserDTO(userDTO);
        }

        if (resDB.getCategories() != null) {
            List<RestaurantDTO.CategoryDTO> categoryDTOs = resDB.getCategories().stream()
                    .map(category -> {
                        RestaurantDTO.CategoryDTO categoryDTO = new RestaurantDTO.CategoryDTO();
                        categoryDTO.setId(category.getId());
                        categoryDTO.setName(category.getName());
                        return categoryDTO;
                    })
                    .collect(Collectors.toList());
            resDTO.setCategoryDTO(categoryDTOs);
        }

        return resDTO;
    }



}
