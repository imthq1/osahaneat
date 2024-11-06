package com.example.demo.Controller;

import com.example.demo.Domain.Role;
import com.example.demo.Service.RoleService;
import com.example.demo.util.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    @PostMapping("/roles")
    @ApiMessage("Create a Role")
    public ResponseEntity<Role> addRole(@RequestBody Role role) throws IdInvalidException {
        if( this.roleService.findByRoleName(role.getRoleName())!= null) {
            throw new IdInvalidException("Role da ton tai!");
        }
        return ResponseEntity.ok().body(this.roleService.createRole(role));
    }
    @GetMapping("/roles/{id}")
    @ApiMessage("Get a Role")
    public ResponseEntity<Role> getRoles(@PathVariable long id) throws IdInvalidException {
        Optional<Role> role = this.roleService.findRoleById(id);
        if (!role.isPresent()) {
            throw new IdInvalidException("Role id invalid");
        }

        return ResponseEntity.ok().body(role.get());
    }
//    @GetMapping("/roles")
//    @ApiMessage("Get All Roles")
//    public ResponseEntity<List<Role>> getAllRoles( ) throws IdInvalidException {
//
//
//    }


}
