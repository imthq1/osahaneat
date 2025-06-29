package com.example.demo.Repository;

import com.example.demo.Domain.Restaurant;
import com.example.demo.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User save(User user);
    User findByEmail(String email);

    User findById(long id);

    User deleteById(long id);
}
