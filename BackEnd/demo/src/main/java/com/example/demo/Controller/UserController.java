package com.example.demo.Controller;

import com.example.demo.Domain.User;
import com.example.demo.Domain.response.ResultPaginationDTO;
import com.example.demo.Domain.response.UserDTO;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import com.example.demo.util.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new User")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user) throws Exception {
        UserDTO userDTO=new UserDTO();
        if(userDTO==null) {
            throw new IdInvalidException("User has been exists!");
        }
        if(this.userRepository.findByEmail(userDTO.getEmail())!=null) {
            throw new IdInvalidException("User has been exists!");
        }
        userDTO=this.userService.CreateUser(user);
    return ResponseEntity.ok().body(userDTO);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get user by id")
    public ResponseEntity<UserDTO> getUser(@PathVariable long id) throws Exception {
        User user=this.userService.findById(id);
        if(user==null)
        {
            throw new IdInvalidException("User not been exists!");
        }

        return ResponseEntity.ok().body(this.userService.userToDTO(user));
    }
    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody User user) throws Exception {
        User userDB=this.userService.findById(user.getId());
        if(userDB==null)
        {
            throw new IdInvalidException("User not been exists!");
        }
        return ResponseEntity.ok().body(this.userService.updateUser(user,userDB));
    }
    @GetMapping("/users")
    @ApiMessage("Get all Users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(@Filter Specification<User> specification, Pageable pageable) throws Exception {
    return ResponseEntity.ok().body(this.userService.getAllUser(specification,pageable));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws Exception {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("User does not exist!"));

        this.userService.DeleteUser(id);
        return ResponseEntity.ok().build();
    }


}
