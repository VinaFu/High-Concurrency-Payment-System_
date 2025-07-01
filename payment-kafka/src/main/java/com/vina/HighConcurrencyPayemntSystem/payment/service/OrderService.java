package com.vina.HighConcurrencyPayemntSystem.payment.service;

import com.vina.HighConcurrencyPayemntSystem.payment.entity.Order;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;

import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    Optional<Order> findByOrderNumber(String orderNumber);
    void updateOrderStatus(String orderNumber, OrderStatus status);
}
