package org.Akorad.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.dto.CreateOrderRequest;
import org.Akorad.dto.CustomerDto;
import org.Akorad.entity.Customer;
import org.Akorad.entity.Order;
import org.Akorad.entity.Product;
import org.Akorad.entity.OrderStatus;
import org.Akorad.repository.CustomerRepository;
import org.Akorad.repository.OrderRepository;
import org.Akorad.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        customer = customerRepository.save(
                new Customer(null, "John", "Doe", "john@example.com", "1234567890")
        );

        product = productRepository.save(
                new Product(null, "Laptop", "Gaming laptop", new BigDecimal("1200.0"), 10)
        );
    }

    @Test
    void placeOrder_success() throws Exception {
        CustomerDto customerDto = new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(),customer.getContactNumber());

        CreateOrderRequest request = new CreateOrderRequest(
                customerDto,
                Collections.singletonList(product.getId()),
                "Main street 1"
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer.id", is(customer.getId().intValue())))
                .andExpect(jsonPath("$.products[0].id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.shippingAddress", is("Main street 1")))
                .andExpect(jsonPath("$.orderStatus", is(OrderStatus.NEW.name())));
    }

    @Test
    void getById_success() throws Exception {
        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(Collections.singletonList(product));
        order.setShippingAddress("Main street 2");
        order.setTotalPrice(new BigDecimal("1200.0"));
        order.setOrderStatus(OrderStatus.NEW);
        order = orderRepository.save(order);

        mockMvc.perform(get("/api/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(order.getId().intValue())))
                .andExpect(jsonPath("$.customer.id", is(customer.getId().intValue())))
                .andExpect(jsonPath("$.products", hasSize(1)));
    }

    @Test
    void updateOrder_success() throws Exception {
        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(Collections.singletonList(product));
        order.setShippingAddress("Old address");
        order.setTotalPrice(new BigDecimal("1200.0"));
        order.setOrderStatus(OrderStatus.NEW);
        order = orderRepository.save(order);

        CustomerDto customerDto = new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(),customer.getContactNumber());

        CreateOrderRequest updateRequest = new CreateOrderRequest(
                customerDto,
                Collections.singletonList(product.getId()),
                "Updated address"
        );

        mockMvc.perform(put("/api/orders/{id}", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingAddress", is("Updated address")));
    }

    @Test
    void deleteOrder_success() throws Exception {
        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(Collections.singletonList(product));
        order.setShippingAddress("Delete street");
        order.setTotalPrice(new BigDecimal("1200.0"));
        order.setOrderStatus(OrderStatus.NEW);
        order = orderRepository.save(order);

        mockMvc.perform(delete("/api/orders/{id}", order.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/{id}", order.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void placeOrder_invalidCustomer_returnsBadRequest() throws Exception {
        CustomerDto customerDto = new CustomerDto(999L, customer.getFirstName(), customer.getLastName(), customer.getEmail(),customer.getContactNumber());

        CreateOrderRequest request = new CreateOrderRequest(
                customerDto, // несуществующий customer
                Collections.singletonList(product.getId()),
                "Invalid street"
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void placeOrder_noProducts_returnsBadRequest() throws Exception {
        CustomerDto customerDto = new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(),customer.getContactNumber());

        CreateOrderRequest request = new CreateOrderRequest(
                customerDto,
                Collections.emptyList(),
                "No products street"
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrder_notFound_returns404() throws Exception {
        CustomerDto customerDto = new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(),customer.getContactNumber());


        CreateOrderRequest request = new CreateOrderRequest(
                customerDto,
                Collections.singletonList(product.getId()),
                "Some street"
        );

        mockMvc.perform(put("/api/orders/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrder_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/orders/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
