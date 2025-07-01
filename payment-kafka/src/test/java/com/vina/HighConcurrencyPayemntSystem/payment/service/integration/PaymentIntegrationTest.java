package com.vina.HighConcurrencyPayemntSystem.payment.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentRequest;
import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentResult;
import com.vina.HighConcurrencyPayemntSystem.payment.entity.Order;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.PaymentChannel;
import com.vina.HighConcurrencyPayemntSystem.payment.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc

public class PaymentIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setup() {
        // clean Redis
        Set<String> keys = redisTemplate.keys("payment:order:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @AfterEach
    void cleanup() {
        orderRepository.deleteAll();
    }

    @Test
    void testProcessPayment_Success() {
        Order order = new Order();
        order.setOrderNumber("1001");
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

        PaymentRequest request = new PaymentRequest();
        request.setOrderNumber("1001");
        request.setPaymentChannel(PaymentChannel.PAYPAL);

        ResponseEntity<PaymentResult> response = restTemplate.postForEntity("/api/payment", request, PaymentResult.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testProcessPayment_AlreadyPaid() {
        Order order = new Order();
        order.setOrderNumber("999");
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        PaymentRequest request = new PaymentRequest();
        request.setOrderNumber("999");
        request.setPaymentChannel(PaymentChannel.PAYPAL);

        ResponseEntity<PaymentResult> response = restTemplate.postForEntity("/api/payment", request, PaymentResult.class);

        assertEquals(HttpStatus.ALREADY_REPORTED, response.getStatusCode());
        assertEquals("Order is already paid via PAYPAL", response.getBody().getMessage());
        assertEquals(OrderStatus.PAID, response.getBody().getOrderStatus());
    }

    @Test
    void testProcessPayment_OrderNotFound() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setOrderNumber(" ");
        request.setPaymentChannel(PaymentChannel.PAYPAL);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/payment", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("\"success\":false");
        assertThat(response.getBody()).contains("Order not found");
    }
}
