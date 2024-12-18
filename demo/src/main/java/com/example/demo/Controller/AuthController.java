package com.example.demo.Controller;

import com.example.demo.Domain.User;
import com.example.demo.Domain.request.ReqLoginDTO;
import com.example.demo.Domain.response.ResLoginDTO;
import com.example.demo.Domain.response.UserDTO;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import com.example.demo.util.ApiMessage;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.constant.Enable;
import com.example.demo.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.RabbitMQ.JobQueue.QUEUE_DEV_REGISTER;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private RabbitTemplate rabbitTemplate;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService, SecurityUtil securityUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, RabbitTemplate rabbitTemplate) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.securityUtil = securityUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${imthang.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/auth/login")
    public ResponseEntity login(@Valid @RequestBody ReqLoginDTO user) {

        User currentUserDB = this.userService.findByEmail(user.getUsername());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        //xac thuc
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();

        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId()
                    , currentUserDB.getEmail()
                    , currentUserDB.getFullname()
                    , currentUserDB.getRole());

            resLoginDTO.setUserLogin(userLogin);
        }

        //create a token => can viet ham loadUserByUsername
        String AccessToken = this.securityUtil.createAcessToken(authentication.getName(), resLoginDTO);

        resLoginDTO.setAccessToken(AccessToken);

        //create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(currentUserDB.getEmail(), resLoginDTO);
        //update user
        this.userService.updateUserToken(refresh_token, currentUserDB.getEmail());

        //set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token1", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().
                header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/login")
    public ResponseEntity logintest(@Valid @RequestBody ReqLoginDTO login) {
        User currentUserDB = this.userService.findByEmail(login.getUsername());
        if (currentUserDB == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        // Kiểm tra mật khẩu
        if (this.passwordEncoder.matches(login.getPassword(), currentUserDB.getPassword())) {

            return ResponseEntity.ok().body(login);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<UserDTO> regiser(@Valid @RequestBody User postManUser) throws IdInvalidException {
        if(userRepository.findByEmail(postManUser.getEmail()) != null)
        {
            throw new IdInvalidException("User not exists!");
        }

        UserDTO userDTO=this.userService.CreateUser(postManUser);


        return ResponseEntity.ok().body(userDTO);
    }
    @PostMapping("/auth/verify")
    @ApiMessage("verify account")
    public ResponseEntity<?> verify(@RequestParam("token") String token) {
        try {
            Jwt jwt = this.securityUtil.checkValidRefreshToken(token);

            String email = jwt.getSubject().toString().replace("\"", "");
            User user = this.userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with email " + email + " not found.");
            }

            user.setEnable(Enable.ENABLED);
            this.userService.save(user);

            ResLoginDTO resLoginDTO = new ResLoginDTO();
            resLoginDTO.setAccessToken(token);

            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    user.getId(),
                    user.getEmail(),
                    user.getFullname(),
                    user.getRole()
            );
            resLoginDTO.setUserLogin(userLogin);

            return ResponseEntity.ok(resLoginDTO);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token verification failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during verification.");
        }
    }



}

