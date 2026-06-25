package com.luanmvcosta0.desafio_tecnico_engeman_be.model;

import com.luanmvcosta0.desafio_tecnico_engeman_be.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "USER_TABLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private Role role;

}
