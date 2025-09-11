package org.Akorad.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    @Valid
    private CustomerDto customer;

    @NotEmpty
    @Valid
    private List<Long> productIds;
    @NotBlank
    private String shippingAddress;
}
