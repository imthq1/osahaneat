package com.example.demo.Controller;

import com.example.demo.Domain.User;
import com.example.demo.Domain.response.UserDTO;
import com.example.demo.Service.UserService;
import com.example.demo.util.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/users")
    @ApiMessage("Create a new User")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user) throws Exception {
        UserDTO userDTO=this.userService.CreateUser(user);
        if(userDTO==null) {
            throw new IdInvalidException("nguoi dung da ton tai!");
        }
    return ResponseEntity.ok().body(userDTO);
    }


}
