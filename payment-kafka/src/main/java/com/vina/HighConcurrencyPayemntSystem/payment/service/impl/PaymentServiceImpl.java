package com.vina.HighConcurrencyPayemntSystem.payment.service.impl;

import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentRequest;
import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentResult;
import com.vina.HighConcurrencyPayemntSystem.payment.entity.Order;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.PaymentChannel;
import com.vina.HighConcurrencyPayemntSystem.payment.exception.InvalidPaymentChannelException;
import com.vina.HighConcurrencyPayemntSystem.payment.exception.OrderAlreadyPaidException;
import com.vina.HighConcurrencyPayemntSystem.payment.exception.OrderNotFoundException;
import com.vina.HighConcurrencyPayemntSystem.payment.repository.OrderRepository;
import com.vina.HighConcurrencyPayemntSystem.payment.service.PaymentService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.EnumSet;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String REDIS_KEY_PREFIX = "payment:order:";
    private static final String REDIS_PROCESSED_VALUE = "processed";
    private static final Duration REDIS_EXPIRATION = Duration.ofHours(1);

    private final OrderRepository orderRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, PaymentResult> kafkaTemplate;

    public PaymentServiceImpl(OrderRepository orderRepository,
                              RedisTemplate<String, String> redisTemplate,
                              KafkaTemplate<String, PaymentResult> kafkaTemplate){
        this.orderRepository = orderRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public PaymentResult processPayment(PaymentRequest request){
        if (request == null || request.getOrderNumber() == null || request.getPaymentChannel() == null) {
            throw new IllegalArgumentException("PaymentRequest or required fields cannot be null");
        }

        String redisKey = REDIS_KEY_PREFIX + request.getOrderNumber();

        if (redisTemplate.hasKey(redisKey)) {
            throw new OrderAlreadyPaidException("Order already processed (via Redis check): " + request.getOrderNumber());
        }

        // 正常的功能部分
        Order order = orderRepository.findByOrderNumber(request.getOrderNumber())
                .orElseThrow(() -> new OrderNotFoundException("Order not found" + request.getOrderNumber()));

        if (!EnumSet.allOf(PaymentChannel.class).contains(request.getPaymentChannel())) {
            throw new InvalidPaymentChannelException("Invalid payment channel: " + request.getPaymentChannel());
        }

        if(order.getStatus() == OrderStatus.PAID){
            throw new OrderAlreadyPaidException("Order is already paid via " + request.getPaymentChannel());
        }

        if(order.getStatus() != OrderStatus.CREATED){
            throw new IllegalStateException("Order is not in payable status!");
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        PaymentResult result = new PaymentResult(true, "Payment successful via " + request.getPaymentChannel(), OrderStatus.PAID);
        kafkaTemplate.send("payment-result", order.getOrderNumber(), result);

        redisTemplate.opsForValue().set(redisKey,  REDIS_PROCESSED_VALUE, REDIS_EXPIRATION);

        return result;
    }

}
