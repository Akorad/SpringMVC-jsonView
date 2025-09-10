package org.Akorad.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.Akorad.dto.Views;

import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonView(Views.UserSummary.class)
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @JsonView(Views.UserSummary.class)
    private String name;

    @Email (message = "Некорректный формат email")
    @NotBlank (message = "Email не может быть пустым")
    @JsonView(Views.UserSummary.class)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonView(Views.UserDetails.class)
    private List<Order> orders;
}
