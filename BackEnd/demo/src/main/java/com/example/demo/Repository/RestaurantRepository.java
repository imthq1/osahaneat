package com.example.demo.Repository;

import com.example.demo.Domain.Restaurant;

import com.example.demo.Domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {
    Restaurant findByLogo(String logo);

    Restaurant findById(long id);

    Restaurant findByName(String name);

    Restaurant findByUser(User user);

    List<Restaurant> findByIdIn(List<Long> ids);

    Restaurant save(Restaurant restaurant);

    void deleteById(long id);

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'PENDING'")
    Page<Restaurant> findPendingRestaurants(Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED'")
    Page<Restaurant> findApprovedRestaurants(Specification<Restaurant> specification, Pageable pageable);
}