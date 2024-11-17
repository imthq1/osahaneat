package com.example.demo.Domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private long id;
    private String email;
    private String fullname;
    private String address;
    private String RoleName;
}
