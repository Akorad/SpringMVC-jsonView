package org.Akorad.service;

import org.Akorad.entity.Product;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(BigDecimal.valueOf(1500));
        product.setQuantityInStock(10);
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<Product> products = productService.getAllProducts();

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_Found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product found = productService.getProductById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(99L));

        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void testCreateProduct() {
        Product newProduct = new Product();
        newProduct.setName("Phone");
        newProduct.setDescription("Smartphone");
        newProduct.setPrice(BigDecimal.valueOf(800));
        newProduct.setQuantityInStock(20);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product created = productService.createProduct(newProduct);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Laptop"); // возвращает mock-объект
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        Product updatedProduct = new Product();
        updatedProduct.setName("Smart TV");
        updatedProduct.setDescription("4K LED TV");
        updatedProduct.setPrice(BigDecimal.valueOf(1200));
        updatedProduct.setQuantityInStock(7);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertThat(result.getName()).isEqualTo("Smart TV");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(1200));
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Product updatedProduct = new Product();
        updatedProduct.setName("Nonexistent");

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(99L, updatedProduct));

        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProduct(99L));

        verify(productRepository, never()).delete(any());
    }
}
