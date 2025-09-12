package org.Akorad.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.dto.CreateOrderRequest;
import org.Akorad.dto.CustomerDto;
import org.Akorad.entity.Customer;
import org.Akorad.entity.Order;
import org.Akorad.entity.OrderStatus;
import org.Akorad.entity.Product;
import org.Akorad.exception.InsufficientStockException;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.CustomerRepository;
import org.Akorad.repository.OrderRepository;
import org.Akorad.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceImplTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerService customerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setContactNumber("77-88-99");

        product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1000.00"));
        product.setQuantityInStock(5);
    }

    @Test
    void placeOrder_success() {
        // given
        CreateOrderRequest req = new CreateOrderRequest(
                new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), "test@test.com", customer.getContactNumber()),
                List.of(product.getId()),
                "Main street 1"
        );

        when(customerService.getCustomerById(customer.getId())).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = orderService.placeOrder(req);

        // then
        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(1, result.getProducts().size());
        assertEquals(new BigDecimal("1000.00"), result.getTotalPrice());
        assertEquals(OrderStatus.NEW, result.getOrderStatus());

        verify(orderRepository).save(any(Order.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void placeOrder_productNotFound() {
        CreateOrderRequest req = new CreateOrderRequest(
                new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), "test@test.com", customer.getContactNumber()),
                List.of(999L),
                "Main street 1"
        );

        when(customerService.getCustomerById(customer.getId())).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder(req));
    }

    @Test
    void placeOrder_insufficientStock() {
        product.setQuantityInStock(0);

        CreateOrderRequest req = new CreateOrderRequest(
                new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), "test@test.com", customer.getContactNumber()),
                List.of(product.getId()),
                "Main street 1"
        );

        when(customerService.getCustomerById(customer.getId())).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(req));
    }

    @Test
    void getById_success() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getById(1L);

        assertEquals(order, result);
    }

    @Test
    void getById_notFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getById(1L));
    }

    @Test
    void updateOrder_success() {
        Order existingOrder = new Order();
        existingOrder.setId(1L);

        when(customerService.getCustomerById(customer.getId())).thenReturn(customer);
        when(productRepository.findAllById(List.of(product.getId()))).thenReturn(List.of(product));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderRequest req = new CreateOrderRequest(
                new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), "test@test.com", customer.getContactNumber()),
                List.of(product.getId()),
                "Main street 1"
        );

        Order result = orderService.updateOrder(1L, req);

        assertEquals(customer, result.getCustomer());
        assertEquals(1, result.getProducts().size());
        assertEquals(new BigDecimal("1000.00"), result.getTotalPrice());
    }

    @Test
    void updateOrder_noProductsFound() {
        when(customerService.getCustomerById(customer.getId())).thenReturn(customer);
        when(productRepository.findAllById(List.of(product.getId()))).thenReturn(List.of());

        CreateOrderRequest req = new CreateOrderRequest(
                new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), "test@test.com", customer.getContactNumber()),
                List.of(product.getId()),
                "Main street 1"
        );

        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L, req));
    }

    @Test
    void deleteOrder_success() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository).delete(order);
    }
}
