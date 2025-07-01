package com.vina.HighConcurrencyPayemntSystem.payment.exception;

import com.vina.HighConcurrencyPayemntSystem.payment.DTO.PaymentResult;
import com.vina.HighConcurrencyPayemntSystem.payment.enums.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<PaymentResult> handleOrderNotFound(OrderNotFoundException ex){
        PaymentResult result = new PaymentResult(false, ex.getMessage(), OrderStatus.FAILED);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    @ExceptionHandler(OrderAlreadyPaidException.class)
    public ResponseEntity<PaymentResult> handleOrderAlreadyPaid(OrderAlreadyPaidException ex){
        PaymentResult result = new PaymentResult(false, ex.getMessage(), OrderStatus.PAID);
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(result);
    }

    @ExceptionHandler(InvalidPaymentChannelException.class)
    public ResponseEntity<PaymentResult> handleInvalidPaymentChannel(InvalidPaymentChannelException ex){
        PaymentResult result = new PaymentResult(false, ex.getMessage(), OrderStatus.FAILED);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<PaymentResult> handleIllegalStatus(IllegalStateException ex){
        PaymentResult result = new PaymentResult(false, ex.getMessage(), OrderStatus.FAILED);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PaymentResult> handleOtherExceptions(Exception ex){
        PaymentResult result = new PaymentResult(false, "Internal server error", OrderStatus.FAILED);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

}
