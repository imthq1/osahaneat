package com.example.demo.Controller;

import com.example.demo.Domain.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.UserService;
import com.example.demo.util.ApiMessage;
import com.example.demo.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private EmailService emailService;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    public EmailController(EmailService emailService
            , UserService userService
            , SecurityUtil securityUtil
    , PasswordEncoder passwordEncoder,
                           UserRepository userRepository) {
        this.emailService = emailService;
        this.userService = userService;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    @PostMapping("/forget/pass")
    @ApiMessage("Forget a password")
    public ResponseEntity<String> forgetPass(@RequestParam("email") String email) {
        User user = this.userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not exits!");
        }
        this.emailService.sendLinkReset(email);
        return ResponseEntity.ok().body("The code has been sent your email .");
    }
    @PostMapping("/reset-password")
    @ApiMessage("Reset Password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("newPassword") String newPassword) {
        try {
            Jwt jwt = this.securityUtil.checkValidRefreshToken(token);
            String email = jwt.getSubject();

            User user = this.userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not exists");
            }

            String hashPassword = this.passwordEncoder.encode(newPassword);

            user.setPassword(hashPassword);

            user.setRefreshToken(null);
            SecurityContextHolder.clearContext();
            this.userRepository.save(user);

            return ResponseEntity.ok().body("Reset password successful.!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Link Invalid or expired!");
        }
    }

}
