package com.example.demo.Domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name khong duoc de trong")
    private String name;

    @NotBlank(message = "apiPath khong duoc de trong")
    private String apiPath;

    @NotBlank(message = "method khong duoc de trong")
    private String method;

    @NotBlank(message = "module khong duoc de trong")
    private String module;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "permissions")
    private List<Role> roles;

}
