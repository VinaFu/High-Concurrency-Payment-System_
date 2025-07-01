package com.vina.HighConcurrencyPayemntSystem.payment.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message){
        super(message);
    }
}
