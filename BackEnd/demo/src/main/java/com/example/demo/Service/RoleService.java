package com.example.demo.Service;

import com.example.demo.Domain.Role;
import com.example.demo.Repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService  {
    private RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public Role findByRoleName(String roleName) {
        return this.roleRepository.findByRoleName(roleName);
    }
    public Role createRole(Role role) {
        return this.roleRepository.save(role);
    }
    public Optional<Role> findRoleById(Long id) {
        return this.roleRepository.findById(id);
    }

}
