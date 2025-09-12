package org.Akorad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.entity.Product;
import org.Akorad.repository.ProductRepository;
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
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(BigDecimal.valueOf(1500));
        product.setQuantityInStock(10);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500));

        assertThat(productRepository.findAll()).hasSize(1);
    }

    @Test
    void testGetProductById_Success() throws Exception {
        Product product = new Product();
        product.setName("Phone");
        product.setDescription("Smartphone");
        product.setPrice(BigDecimal.valueOf(800));
        product.setQuantityInStock(20);
        product = productRepository.save(product);

        mockMvc.perform(get("/api/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    void testGetAllProducts_Success() throws Exception {
        Product product = new Product();
        product.setName("Tablet");
        product.setDescription("Android tablet");
        product.setPrice(BigDecimal.valueOf(300));
        product.setQuantityInStock(15);
        productRepository.save(product);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tablet"))
                .andExpect(jsonPath("$[0].price").value(300));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        Product product = new Product();
        product.setName("TV");
        product.setDescription("LED TV");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setQuantityInStock(5);
        product = productRepository.save(product);

        Product updated = new Product();
        updated.setName("Smart TV");
        updated.setDescription("4K LED TV");
        updated.setPrice(BigDecimal.valueOf(1200));
        updated.setQuantityInStock(7);

        mockMvc.perform(put("/api/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Smart TV"))
                .andExpect(jsonPath("$.price").value(1200));

        assertThat(productRepository.findById(product.getId()).get().getName()).isEqualTo("Smart TV");
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        Product product = new Product();
        product.setName("Camera");
        product.setDescription("Digital camera");
        product.setPrice(BigDecimal.valueOf(500));
        product.setQuantityInStock(3);
        product = productRepository.save(product);

        mockMvc.perform(delete("/api/products/" + product.getId()))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(product.getId())).isEmpty();
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProduct_InvalidData() throws Exception {
        Product invalidProduct = new Product();
        invalidProduct.setName("");
        invalidProduct.setDescription("Broken item");
        invalidProduct.setPrice(BigDecimal.valueOf(-10));
        invalidProduct.setQuantityInStock(-5);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }
}
