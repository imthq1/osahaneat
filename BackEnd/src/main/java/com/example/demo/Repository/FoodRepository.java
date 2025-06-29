package com.example.demo.Repository;


import com.example.demo.Domain.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FoodRepository extends JpaRepository<Food, Long>, JpaSpecificationExecutor<Food> {
    Food findById(long id);
    Food findByName(String name);
    List<Food> findByIdIn(List<Long> ids);

    Page<Food> findByRestaurant_Id(Long restaurantId, Pageable pageable);



}
