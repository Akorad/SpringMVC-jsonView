package org.Akorad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.entity.Order;
import org.Akorad.entity.User;
import org.Akorad.repository.OrderRepository;
import org.Akorad.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user = userRepository.save(user);
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        Order order = new Order();
        order.setProduct("Laptop");
        order.setAmount(BigDecimal.valueOf(100.50));
        order.setStatus("NEW");

        mockMvc.perform(post("/api/orders/user/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$[0].product").doesNotExist())
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.status").value("NEW"));

        assertThat(orderRepository.findAll()).hasSize(1);
    }

    @Test
    void testGetOrderById_DetailsView() throws Exception {
        Order order = new Order();
        order.setProduct("Laptop");
        order.setAmount(BigDecimal.valueOf(300));
        order.setStatus("SHIPPED");
        order.setUser(user);
        order = orderRepository.save(order);

        mockMvc.perform(get("/api/orders/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.product").value("Laptop"))
                .andExpect(jsonPath("$.amount").value(300))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void testGetAllOrders_ReturnsList() throws Exception {
        Order order = new Order();
        order.setProduct("Laptop");
        order.setAmount(BigDecimal.valueOf(200));
        order.setStatus("PAID");
        order.setUser(user);
        orderRepository.save(order);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PAID"))
                .andExpect(jsonPath("$[0].amount").value(200));
    }

    @Test
    void testGetOrderById_Found() throws Exception {
        Order order = new Order();
        order.setProduct("Laptop");
        order.setAmount(BigDecimal.valueOf(300));
        order.setStatus("SHIPPED");
        order.setUser(user);
        order = orderRepository.save(order);

        mockMvc.perform(get("/api/orders/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"))
                .andExpect(jsonPath("$.amount").value(300));
    }

    @Test
    void testUpdateOrder_Success() throws Exception {
        Order order = new Order();
        order.setProduct("Laptop");
        order.setAmount(BigDecimal.valueOf(400));
        order.setStatus("NEW");
        order.setUser(user);
        order = orderRepository.save(order);

        Order updateRequest = new Order();
        updateRequest.setProduct("Laptop");
        updateRequest.setAmount(BigDecimal.valueOf(450));
        updateRequest.setStatus("UPDATED");

        mockMvc.perform(put("/api/orders/" + order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(450))
                .andExpect(jsonPath("$.status").value("UPDATED"));

        assertThat(orderRepository.findById(order.getId()).get().getStatus()).isEqualTo("UPDATED");
    }

    @Test
    void testDeleteOrder_Success() throws Exception {
        Order order = new Order();
        order.setProduct("Laptop");
        order.setAmount(BigDecimal.valueOf(500));
        order.setStatus("TO_DELETE");
        order.setUser(user);
        order = orderRepository.save(order);

        mockMvc.perform(delete("/api/orders/" + order.getId()))
                .andExpect(status().isNoContent());

        assertThat(orderRepository.findById(order.getId())).isEmpty();
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        mockMvc.perform(get("/api/orders/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateOrder_SuccessDetailsView() throws Exception {
        Order order = new Order();
        order.setProduct("Laptop");
        order.setAmount(BigDecimal.valueOf(100.50));
        order.setStatus("NEW");

        mockMvc.perform(post("/api/orders/user/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product").value("Laptop"))
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.status").value("NEW"));
    }
}
