package com.example.demo.Repository;

import com.example.demo.Domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
public interface FoodService extends JpaRepository<Food, Long>, JpaSpecificationExecutor<Food> {

}
