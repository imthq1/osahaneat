package com.example.demo.Repository;

import com.example.demo.Domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> , JpaSpecificationExecutor<Category> {
    List<Category> findByIdIn(List<Long> ids);

    Category save(Category category);

    Category findByName(String name);
    List<Category> findByRestaurantId(Long restaurantId);
}
