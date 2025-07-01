package com.vina.HighConcurrencyPayemntSystem.payment.controller;

import com.vina.HighConcurrencyPayemntSystem.payment.entity.Order;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import com.vina.HighConcurrencyPayemntSystem.payment.exception.OrderNotFoundException;
import com.vina.HighConcurrencyPayemntSystem.payment.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order){
        Order createOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createOrder);
    }

    @GetMapping("/{orderNumber}")
    public Order findByOrderNumber(@PathVariable String orderNumber){
        return orderService.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderNumber));
    }

    @PutMapping("/{orderNumber}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable String orderNumber, @RequestParam OrderStatus status){
        Order order= orderService.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderNumber));
        orderService.updateOrderStatus(orderNumber, status);
        return ResponseEntity.noContent().build();
    }
}
