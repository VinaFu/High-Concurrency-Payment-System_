package com.vina.HighConcurrencyPayemntSystem.payment.exception;

public class OrderAlreadyPaidException extends RuntimeException {
    public OrderAlreadyPaidException(String message){
        super(message);
    }
}
