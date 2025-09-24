package org.Akorad.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Email
    @Column(unique = true, nullable = true)
    private String email;

    private Boolean isAccountNonLocked = true;

    private Integer failedAttempt = 0;

}
