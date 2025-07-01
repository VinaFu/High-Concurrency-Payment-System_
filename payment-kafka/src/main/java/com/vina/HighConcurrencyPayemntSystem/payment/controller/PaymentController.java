package com.vina.HighConcurrencyPayemntSystem.payment.controller;

import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentRequest;
import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentResult;
import com.vina.HighConcurrencyPayemntSystem.payment.repository.OrderRepository;
import com.vina.HighConcurrencyPayemntSystem.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    public PaymentController(PaymentService paymentService, OrderRepository orderRepository){
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResult> proceedPayment(@RequestBody PaymentRequest request){
        PaymentResult result = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
