package com.example.demo.Domain.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestaurantDTO {
    private long id;
    private String name;
    private String address;
    private String description;
    private int rating;
    private String logo;
    private String content;
    private String status;
    private UserDTO userDTO;
    private List<CategoryDTO> categoryDTO;
    private List<ImageDTO> imageDTOS;
    @Getter
    @Setter
    public static class UserDTO {
        private long id;
        private String email;
        private String fullname;
        private String address;
    }
    @Getter
    @Setter
    public static class CategoryDTO {
        private long id;
        private String name;
    }
    @Getter
    @Setter
    public static class ImageDTO{
        private String path;
    }

}


