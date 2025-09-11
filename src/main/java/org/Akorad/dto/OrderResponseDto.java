package org.Akorad.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.Akorad.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private CustomerDto customer;
    private List<Product> products;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private double totalPrice;
    private String orderStatus;
}
