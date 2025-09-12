package org.Akorad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.entity.Customer;
import org.Akorad.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();

        customer = new Customer();
        customer.setFirstName("Alice");
        customer.setLastName("Syrian");
        customer.setEmail("alice@example.com");
        customer.setContactNumber("+77-88-99");
        customer = customerRepository.save(customer);
    }


    @Test
    void testGetAllCustomers_ReturnsList() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }

    @Test
    void testGetCustomerById_Found() throws Exception {
        mockMvc.perform(get("/api/customers/" + customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void testGetCustomerById_NotFound() throws Exception {
        mockMvc.perform(get("/api/customers/99999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void testCreateCustomer_Success() throws Exception {
        Customer newCustomer = new Customer();
        newCustomer.setFirstName("Bob");
        newCustomer.setLastName("Bobov");
        newCustomer.setEmail("bob@example.com");
        newCustomer.setContactNumber("+77-88-99");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"));

        assertThat(customerRepository.findAll()).hasSize(2);
    }

    @Test
    void testCreateCustomer_InvalidRequest() throws Exception {
        Customer invalidCustomer = new Customer();
        invalidCustomer.setFirstName(""); // пустое имя
        invalidCustomer.setLastName("");
        invalidCustomer.setEmail("invalid-email"); // невалидный email
        invalidCustomer.setContactNumber("");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCustomer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCustomer_Success() throws Exception {
        Customer updateRequest = new Customer();
        updateRequest.setFirstName("Alice Updated");
        updateRequest.setLastName("Syrian Updated");
        updateRequest.setEmail("alice_updated@example.com");
        updateRequest.setContactNumber("+99-88-99");

        mockMvc.perform(put("/api/customers/" + customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice Updated"))
                .andExpect(jsonPath("$.email").value("alice_updated@example.com"));

        Optional<Customer> updated = customerRepository.findById(customer.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getFirstName()).isEqualTo("Alice Updated");
    }

    @Test
    void testUpdateCustomer_NotFound() throws Exception {
        Customer updateRequest = new Customer();
        updateRequest.setFirstName("Ghost");
        updateRequest.setLastName("Ghost");
        updateRequest.setEmail("ghost@example.com");
        updateRequest.setContactNumber("+00-00-00");

        mockMvc.perform(put("/api/customers/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCustomer_Success() throws Exception {
        mockMvc.perform(delete("/api/customers/" + customer.getId()))
                .andExpect(status().isNoContent());

        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }

    @Test
    void testDeleteCustomer_NotFound() throws Exception {
        mockMvc.perform(delete("/api/customers/99999"))
                .andExpect(status().isNotFound());
    }
}
