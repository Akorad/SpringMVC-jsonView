package org.Akorad.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.Akorad.dto.Views;
import org.Akorad.entity.Order;
import org.Akorad.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @JsonView(Views.UserDetails.class) // показываем детали
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @JsonView(Views.UserDetails.class)
    public Order getOrderById(@PathVariable("id") Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.UserDetails.class)
    public Order createOrder(@PathVariable("userId") Long userId, @Valid @RequestBody Order order) {
        return orderService.createOrder(userId, order);
    }

    @PutMapping("/{id}")
    @JsonView(Views.UserDetails.class)
    public Order updateOrder(@PathVariable("id") Long id, @Valid @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
    }
}
