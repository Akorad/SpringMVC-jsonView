package org.Akorad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.Akorad.dto.CreateOrderRequest;
import org.Akorad.dto.OrderResponseDto;
import org.Akorad.entity.Order;
import org.Akorad.entity.Product;
import org.Akorad.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody @Valid CreateOrderRequest req) {
        Order saved = orderService.placeOrder(req);
        OrderResponseDto resp = objectMapper.convertValue(saved, OrderResponseDto.class);
        // конвертация вложенных продуктов
        resp.setProducts(saved.getProducts().stream()
                .map(p -> objectMapper.convertValue(p, Product.class))
                .collect(Collectors.toList()));
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{id}")
    public OrderResponseDto getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        OrderResponseDto resp = objectMapper.convertValue(order, OrderResponseDto.class);
        resp.setProducts(order.getProducts().stream()
                .map(p -> objectMapper.convertValue(p, Product.class))
                .collect(Collectors.toList()));
        return resp;
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
