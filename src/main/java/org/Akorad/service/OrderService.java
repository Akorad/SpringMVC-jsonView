package org.Akorad.service;

import org.Akorad.dto.CreateOrderRequest;
import org.Akorad.dto.OrderResponseDto;
import org.Akorad.entity.Order;

public interface OrderService {
    OrderResponseDto placeOrder(CreateOrderRequest req);
    OrderResponseDto  getById(Long id);
    OrderResponseDto updateOrder(Long id, CreateOrderRequest request);
    void deleteOrder(Long id);
}
