package com.example.demo.Repository;

import com.example.demo.Domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Role save(Role role);
    Role findByRoleName(String name);
    Optional<Role> findById(Long id);
    List<Role> findAll();
}
