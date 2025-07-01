package com.vina.HighConcurrencyPayemntSystem.payment.DTO;

import com.vina.HighConcurrencyPayemntSystem.payment.enums.PaymentChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String orderNumber;
    private PaymentChannel paymentChannel;
}
