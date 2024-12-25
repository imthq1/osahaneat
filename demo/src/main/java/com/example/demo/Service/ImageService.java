package com.example.demo.Service;

import com.example.demo.Domain.Image;
import com.example.demo.Domain.Restaurant;
import com.example.demo.Repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {
private final ImageRepository imageRepository;
public ImageService(ImageRepository imageRepository) {
    this.imageRepository = imageRepository;
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
