package com.vina.HighConcurrencyPayemntSystem.payment.service;

import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentRequest;
import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentResult;

public interface PaymentService {
    PaymentResult processPayment(PaymentRequest request);
}
