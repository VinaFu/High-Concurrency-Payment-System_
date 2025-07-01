package com.vina.HighConcurrencyPayemntSystem.payment.DTO;

import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResult {
    private boolean success;
    private String message;
    private OrderStatus orderStatus;
}
