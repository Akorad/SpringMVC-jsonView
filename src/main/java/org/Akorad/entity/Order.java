package org.Akorad.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.Akorad.dto.Views;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.UserDetails.class)
    private Long id;


    @NotBlank(message = "Название продукта не может быть пустым")
    @JsonView(Views.UserDetails.class)
    private String product;

    @NotNull(message = "Сумма заказа не может быть пустой")
    @JsonView(Views.UserDetails.class)
    private BigDecimal amount;

    @NotBlank(message = "Статус заказа не может быть пустым")
    @JsonView(Views.UserDetails.class)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
