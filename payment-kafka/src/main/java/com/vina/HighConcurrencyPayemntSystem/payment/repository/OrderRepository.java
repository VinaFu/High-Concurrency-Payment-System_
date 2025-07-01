package com.vina.HighConcurrencyPayemntSystem.payment.repository;

import com.vina.HighConcurrencyPayemntSystem.payment.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
}
