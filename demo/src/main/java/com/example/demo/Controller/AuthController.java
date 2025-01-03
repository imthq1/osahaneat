package com.example.demo.Controller;

import com.example.demo.Domain.User;
import com.example.demo.Domain.request.ReqLoginDTO;
import com.example.demo.Domain.response.ResLoginDTO;
import com.example.demo.Domain.response.UserDTO;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.UserService;
import com.example.demo.config.oauth2.GoogleUtils;
import com.example.demo.config.oauth2.user.GooglePojo;
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

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleUtils googleUtils;
    private RabbitTemplate rabbitTemplate;
    private final EmailService emailService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService, SecurityUtil securityUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          RabbitTemplate rabbitTemplate,
                          EmailService emailService,
                          GoogleUtils googleUtils) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.securityUtil = securityUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
        this.emailService = emailService;
        this.googleUtils = googleUtils;
    }

    @Value("${imthang.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final Set<String> processedCodes = Collections.synchronizedSet(new HashSet<>());

    private boolean isCodeProcessed(String code) {
        return processedCodes.contains(code);
    }

    private void markCodeAsProcessed(String code) {
        processedCodes.add(code);
    }

    @RequestMapping(value ="/login/oauth2/code/google", method = RequestMethod.GET)
    public ResponseEntity<Object> loginGoogle(HttpServletRequest request) {

            String code = request.getParameter("code");
            if (code == null || code.isEmpty()) {
                return ResponseEntity.badRequest().body("Lỗi: Không lấy được mã xác thực từ Google.");
            }
            if (isCodeProcessed(code)) {
                return ResponseEntity.badRequest().body("Mã code đã được sử dụng.");
            }
        try {
            markCodeAsProcessed(code);
            // Lấy Access Token từ Google
            String accessToken = googleUtils.getToken(code);
            // Lấy thông tin người dùng từ Google
            GooglePojo googlePojo = googleUtils.getUserInfo(accessToken);
            // Xây dựng thông tin người dùng trong hệ thống
            User user = googleUtils.buildUser(googlePojo);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            ResLoginDTO resLoginDTO = new ResLoginDTO();

            if (user != null) {
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                        user.getId()
                        , user.getEmail()
                        , user.getFullname()
                        , user.getRole());

                resLoginDTO.setUserLogin(userLogin);
            }

            String accessTokenJWT = this.securityUtil.createAcessToken(user.getEmail(),resLoginDTO);
            String refreshTokenJWT = this.securityUtil.createRefreshToken(user.getEmail(),resLoginDTO);

            this.userService.updateUserToken(refreshTokenJWT, user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "Đăng nhập thành công.");
            response.put("access_token", accessTokenJWT);
            response.put("user", Map.of("email", user.getEmail(), "name", user.getFullname(), "role", "USER"));

            ResponseCookie resCookies = ResponseCookie.from("refresh_token1", refreshTokenJWT)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(refreshTokenExpiration)
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(response);

        } catch (Exception e) {
            // Ghi log lỗi
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi trong quá trình xử lý.");
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity login(@Valid @RequestBody ReqLoginDTO user) throws IdInvalidException {

        User currentUserDB = this.userService.findByEmail(user.getUsername());
        if(currentUserDB.getEnable().equals(null)){
            throw new IdInvalidException("User not verify");
        }
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
    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout()throws IdInvalidException{
        String email=SecurityUtil.getCurrentUserLogin().isPresent()?SecurityUtil.getCurrentUserLogin().get():"";
        if(email.equals(""))
        {
            throw new IdInvalidException("Access token khong hop le");
        }

        this.userService.updateUserToken(null,email);
        //remove refresh token cookie
        ResponseCookie resCookies=ResponseCookie.from("refresh_token1",null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,resCookies.toString()).body(null);
    }

    @PostMapping("/auth/account")
    @ApiMessage("get Account")
    public ResponseEntity<UserDTO> getAccount() throws IdInvalidException {
        UserDTO userDTO=new UserDTO();
        Optional<String> email=SecurityUtil.getCurrentUserLogin();
        if(email.isPresent()){
            User currentUserDB = this.userService.findByEmail(email.get());
            if(currentUserDB.getRole()==null){
                userDTO.setRoleName("");
            }else
            {
                userDTO.setRoleName(currentUserDB.getRole().getRoleName());
            }
            userDTO.setAddress(currentUserDB.getAddress());
            userDTO.setEmail(currentUserDB.getEmail());
            userDTO.setFullname(currentUserDB.getFullname());
            userDTO.setId(currentUserDB.getId());
        }
        return ResponseEntity.ok(userDTO);
    }
    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<UserDTO> regiser(@Valid @RequestBody User postManUser) throws IdInvalidException {
        if(userRepository.findByEmail(postManUser.getEmail()) != null)
        {
            throw new IdInvalidException("User has been exists!");
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

