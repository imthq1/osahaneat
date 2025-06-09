package com.example.demo.Service;

import com.example.demo.Domain.Restaurant;
import com.example.demo.Domain.Role;
import com.example.demo.Domain.User;

import com.example.demo.Domain.response.ResultPaginationDTO;
import com.example.demo.Domain.response.UserDTO;
import com.example.demo.Repository.RestaurantRepository;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.config.RabbitMQ.JobQueue;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,RabbitTemplate rabbitTemplate,
                       RestaurantRepository restaurantRepository
    ,RestaurantService restaurantService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
    }
    public User save(User user) {
        return this.userRepository.save(user);
    }
    public User findById(long id)
    {
        return this.userRepository.findById(id);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserDTO CreateUser(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setFullname(user.getFullname());

        this.userRepository.save(user);
        if(user.getRole()!=null){
            Optional<Role> role=this.roleRepository.findById(user.getRole().getId());
            if(role.isPresent())
            {
                Role role1=role.get();
                userDTO.setRoleName(role1.getRoleName());
            }
        }else{
            userDTO.setRoleName("USER");
        }
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            rabbitTemplate.convertAndSend(JobQueue.QUEUE_DEV_REGISTER, user.getEmail());
        }, 10, TimeUnit.SECONDS);
        return userDTO;
    }

    public void DeleteUser(long id) {
        Restaurant restaurant=this.restaurantRepository.findByUser(this.userRepository.findById(id));
        this.restaurantService.deleteRestaurant(restaurant.getId());
        this.userRepository.deleteById(id);

    }
    public UserDTO userToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        if (user.getRole() != null) {
            Optional<Role> role = this.roleRepository.findById(user.getRole().getId());
            if (role.isPresent()) {
                userDTO.setRoleName(role.get().getRoleName());
            }
        }
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setFullname(user.getFullname());

        return userDTO;
    }
    public ResultPaginationDTO getAllUser(Specification specification, Pageable pageable)
    {
        Page<User> users= this.userRepository.findAll(specification,pageable);
    ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(users.getTotalPages());
        meta.setTotal(users.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        resultPaginationDTO.setResult(users);

    return resultPaginationDTO;
    }
    public UserDTO updateUser(User user, User userDB) {
        if (user.getRole() != null) {
            Role role = this.roleRepository.findByRoleName(user.getRole().getRoleName());
            userDB.setRole(role);
        }

        if (user.getEmail() != null) {
            userDB.setEmail(user.getEmail());
        }
        if (user.getAddress() != null) {
            userDB.setAddress(user.getAddress());
        }
        if (user.getFullname() != null) {
            userDB.setFullname(user.getFullname());
        }

        this.userRepository.save(userDB);

        return this.userToDTO(userDB);
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
