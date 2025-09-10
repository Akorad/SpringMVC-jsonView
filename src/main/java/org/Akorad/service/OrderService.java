package org.Akorad.service;

import org.Akorad.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order createOrder(Long userId, Order order);
    Order updateOrder(Long id, Order order);
    void deleteOrder(Long id);
}
