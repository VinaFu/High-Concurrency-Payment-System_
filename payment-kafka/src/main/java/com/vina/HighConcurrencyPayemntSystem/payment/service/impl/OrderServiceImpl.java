package com.vina.HighConcurrencyPayemntSystem.payment.service.impl;

import com.vina.HighConcurrencyPayemntSystem.payment.entity.Order;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import com.vina.HighConcurrencyPayemntSystem.payment.exception.OrderNotFoundException;
import com.vina.HighConcurrencyPayemntSystem.payment.repository.OrderRepository;
import com.vina.HighConcurrencyPayemntSystem.payment.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    public OrderServiceImpl(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public void updateOrderStatus(String orderNumber, OrderStatus status) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderNumber));
        order.setStatus(status);
        orderRepository.save(order);
    }

}
