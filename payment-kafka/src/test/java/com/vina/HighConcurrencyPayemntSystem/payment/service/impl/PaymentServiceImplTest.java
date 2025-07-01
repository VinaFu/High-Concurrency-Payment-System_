package com.vina.HighConcurrencyPayemntSystem.payment.service.impl;

import com.vina.HighConcurrencyPayemntSystem.payment.entity.Order;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.PaymentChannel;
import com.vina.HighConcurrencyPayemntSystem.payment.exception.OrderAlreadyPaidException;
import com.vina.HighConcurrencyPayemntSystem.payment.exception.OrderNotFoundException;
import com.vina.HighConcurrencyPayemntSystem.payment.repository.OrderRepository;
import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentRequest;
import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentServiceImpl paymentServiceImpl;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private KafkaTemplate<String, PaymentResult> kafkaTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    void processPayment_Success(){
        Order order = new Order();
        order.setOrderNumber("333");
        order.setStatus(OrderStatus.CREATED);
        PaymentRequest request = new PaymentRequest("333", PaymentChannel.PAYPAL);

        when(orderRepository.findByOrderNumber("333")).thenReturn(Optional.of(order));

        when(redisTemplate.hasKey("payment:order:333")).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(kafkaTemplate.send("payment-result", "333", new PaymentResult(true, "Payment successful via PAYPAL", OrderStatus.PAID)))
                .thenReturn(null);

        PaymentResult actual = paymentServiceImpl.processPayment(request);

        assertTrue(actual.isSuccess());
        assertEquals("Payment successful via " + request.getPaymentChannel(),actual.getMessage());
        assertEquals(OrderStatus.PAID, order.getStatus());

        verify(orderRepository).findByOrderNumber("333");
        verify(orderRepository).save(order);
    }

    @Test
    void processPayment_Empty(){
        PaymentRequest request = new PaymentRequest("",PaymentChannel.PAYPAL );
        when(orderRepository.findByOrderNumber(request.getOrderNumber())).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () ->{
           paymentServiceImpl.processPayment(request);
        });

        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository).findByOrderNumber("");
    }

    @Test
    void processPayment_Paid(){
        Order order = new Order();
        order.setOrderNumber("111");
        order.setStatus(OrderStatus.PAID);

        PaymentRequest request = new PaymentRequest("111", PaymentChannel.PAYPAL);

        when(orderRepository.findByOrderNumber(order.getOrderNumber())).thenReturn(Optional.of(order));

        OrderAlreadyPaidException exception = assertThrows(OrderAlreadyPaidException.class, ()->{
            paymentServiceImpl.processPayment(request);
        });

        assertEquals("Order is already paid via "+ request.getPaymentChannel(), exception.getMessage());
        assertEquals(OrderStatus.PAID, order.getStatus());

        verify(orderRepository).findByOrderNumber("111");
    }

    @Test
    void processPayment_NotCreated(){
        Order order = new Order();
        order.setOrderNumber("222");
        order.setStatus(OrderStatus.PROCESSING);
        PaymentRequest request = new PaymentRequest("222", PaymentChannel.PAYPAL);

        when(orderRepository.findByOrderNumber("222")).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class, ()-> {
            paymentServiceImpl.processPayment(request);
        });

        assertEquals("Order is not in payable status!", exception.getMessage());
        assertNotEquals(OrderStatus.PAID, order.getStatus());

        verify(orderRepository).findByOrderNumber("222");
    }
}
