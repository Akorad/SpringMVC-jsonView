package org.Akorad.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
