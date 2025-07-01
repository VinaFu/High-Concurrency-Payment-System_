package com.vina.HighConcurrencyPayemntSystem.payment.service.impl;

import com.vina.HighConcurrencyPayemntSystem.payment.entity.Order;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import com.vina.HighConcurrencyPayemntSystem.payment.exception.OrderNotFoundException;
import com.vina.HighConcurrencyPayemntSystem.payment.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Test
    void createOrder_shouldReturnSavedOrder() {
        Order order = new Order();
        order.setOrderNumber("123");

        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderServiceImpl.createOrder(order);

        assertEquals(order, result);

        verify(orderRepository).save(order);
    }

    @Test
    void findByOrderNumber_shouldReturnOrderIfExists() {
        Order order = new Order();
        order.setOrderNumber("123");

        when(orderRepository.findByOrderNumber(order.getOrderNumber())).thenReturn(Optional.of(order));

        Optional<Order> result = orderServiceImpl.findByOrderNumber("123");

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        verify(orderRepository).findByOrderNumber("123");
    }

    @Test
    void updateOrderStatus_shouldUpdateStatusWhenOrderExists() {
        Order order = new Order();
        order.setOrderNumber("123");
        order.setStatus(OrderStatus.CREATED);

        when(orderRepository.findByOrderNumber("123")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderServiceImpl.updateOrderStatus("123", OrderStatus.PAID);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        assertEquals(OrderStatus.PAID, orderCaptor.getValue().getStatus());
    }

    @Test
    void updateOrderStatus_shouldThrowWhenOrderNotFound() {
        when(orderRepository.findByOrderNumber("000")).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> {
            orderServiceImpl.updateOrderStatus("000", OrderStatus.PAID);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
    }

}




