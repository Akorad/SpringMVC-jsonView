package org.Akorad.service;

import org.Akorad.entity.Order;
import org.Akorad.entity.User;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.OrderRepository;
import org.Akorad.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("John Doe");

        order = new Order();
        order.setId(1L);
        order.setAmount(BigDecimal.valueOf(100.0));
        order.setStatus("NEW");
        order.setUser(user);
    }

    @Test
    void testGetAllOrders_ReturnsList() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAmount()).isEqualTo(100.0);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderById_Found() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("NEW");
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with id 1");
    }

    @Test
    void testCreateOrder_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.save(ArgumentMatchers.any())).thenReturn(order);

        Order result = orderService.createOrder(1L, order);

        assertThat(result.getUser()).isEqualTo(user);
        verify(orderRepository).save(order);
    }

    @Test
    void testCreateOrder_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(1L, order))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id 1");
    }

    @Test
    void testUpdateOrder_Success() {
        Order orderDetails = new Order();
        orderDetails.setAmount(BigDecimal.valueOf(200.0));
        orderDetails.setStatus("UPDATED");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(ArgumentMatchers.any())).thenReturn(order);

        Order result = orderService.updateOrder(1L, orderDetails);

        assertThat(result.getAmount()).isEqualTo(200.0);
        assertThat(result.getStatus()).isEqualTo("UPDATED");
    }

    @Test
    void testUpdateOrder_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(1L, order))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with id 1");
    }

    @Test
    void testDeleteOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void testDeleteOrder_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deleteOrder(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with id 1");
    }
}
