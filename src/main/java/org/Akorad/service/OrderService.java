package org.Akorad.service;

import org.Akorad.dto.CreateOrderRequest;
import org.Akorad.dto.OrderResponseDto;
import org.Akorad.entity.Order;

public interface OrderService {
    Order placeOrder(CreateOrderRequest req);
    Order  getById(Long id);
    Order updateOrder(Long id, CreateOrderRequest request);
    void deleteOrder(Long id);
}
