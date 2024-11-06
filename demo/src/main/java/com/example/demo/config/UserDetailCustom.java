package com.example.demo.config;

import com.example.demo.Domain.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("userDetailsService")
public class UserDetailCustom implements UserDetailsService {
    private final UserRepository userService;
    public UserDetailCustom(UserRepository userService) {
        this.userService = userService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userService.findByEmail(username);

        // Ghi log để kiểm tra thông tin người dùng
        if (user == null) {
            System.out.println("Không tìm thấy người dùng với email: " + username);
            throw new UsernameNotFoundException("username/password error");
        } else {
            System.out.println("Tìm thấy người dùng với email: " + username);
            System.out.println("Mật khẩu mã hóa từ database: " + user.getPassword());
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}

