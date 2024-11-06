package com.example.demo.Service;

import com.example.demo.Domain.User;
import com.example.demo.Domain.request.ReqLoginDTO;
import com.example.demo.Domain.response.UserDTO;
import com.example.demo.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public UserDTO CreateUser(User user) {
        if(userRepository.findByEmail(user.getEmail()) != null)
        {
            return null;
        }
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setFullname(user.getFullname());
        this.userRepository.save(user);
        return userDTO;
    }
    public User DTOtoUser(ReqLoginDTO userDTO)
    {
        User user = new User();
        user.setEmail(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        return user;
    }
    public void updateUserToken(String token,String email)
    {
        User currentUser = this.findByEmail(email);
        if(currentUser!=null)
        {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }


}
