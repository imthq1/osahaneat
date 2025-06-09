package com.example.demo.Service;

import com.example.demo.Domain.Image;
import com.example.demo.Domain.Restaurant;
import com.example.demo.Repository.ImageRepository;
import com.example.demo.config.cloundinary.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImageService {
private final ImageRepository imageRepository;
private final CloudinaryService cloudinaryService;
public ImageService(ImageRepository imageRepository, CloudinaryService cloudinaryService) {
    this.imageRepository = imageRepository;
    this.cloudinaryService = cloudinaryService;
}
public Image save(Image image) {
    return imageRepository.save(image);
}
public void saveAll(List<Image> images) {
    for(Image image : images) {
        this.imageRepository.save(image);
    }
}
public void saveImageRestaurant(Restaurant restaurant,List<Image> images) {
     images = restaurant.getImageList().stream().map(img -> {
        Image image = new Image();
        image.setPath(img.getPath());
        image.setRestaurant(restaurant);
        return image;
    }).collect(Collectors.toList());
     this.saveAll(images);
}
}
