package org.Akorad.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.Akorad.dto.CreateOrderRequest;
import org.Akorad.dto.OrderResponseDto;
import org.Akorad.entity.Customer;
import org.Akorad.entity.Order;
import org.Akorad.entity.OrderStatus;
import org.Akorad.entity.Product;
import org.Akorad.exception.InsufficientStockException;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.CustomerRepository;
import org.Akorad.repository.OrderRepository;
import org.Akorad.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final CustomerService customerService;

    @Override
    @Transactional
    public Order placeOrder(CreateOrderRequest req) {
        Customer customer = customerService.getCustomerById(req.getCustomer().getId());
        Customer savedCustomer = customerRepository.save(customer);

        Order order = new Order();
        order.setCustomer(savedCustomer);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(req.getShippingAddress());
        order.setOrderStatus(OrderStatus.NEW);

        BigDecimal total = new BigDecimal("0.0");
        List<Product> orderedProducts = new ArrayList<>();

        for (Long productId : req.getProductIds()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

            if (product.getQuantityInStock() < 1) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getId());
            }

            product.setQuantityInStock(product.getQuantityInStock() - 1);
            productRepository.save(product);

            orderedProducts.add(product);
            total = total.add(product.getPrice());
        }

        order.setProducts(orderedProducts);
        order.setTotalPrice(total);

        return orderRepository.save(order);
    }

    @Override
    public Order getById(Long id) {
            return orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    @Override
    public Order updateOrder(Long id, CreateOrderRequest request) {
        Customer customer = customerService.getCustomerById(request.getCustomer().getId());
        List<Product> products = productRepository.findAllById(request.getProductIds());

        if (products.isEmpty()){
            throw new ResourceNotFoundException("No products found for the given IDs");
        }

        BigDecimal totalPrice = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = (id==null) ? new Order() : getById(id);
        order.setCustomer(customer);
        order.setProducts(products);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(request.getShippingAddress());
        order.setTotalPrice(totalPrice);
        order.setOrderStatus(OrderStatus.NEW);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = getById(id);
        orderRepository.delete(order);
    }
}
