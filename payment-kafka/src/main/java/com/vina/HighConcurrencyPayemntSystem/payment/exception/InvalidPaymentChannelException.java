package com.vina.HighConcurrencyPayemntSystem.payment.exception;

public class InvalidPaymentChannelException extends RuntimeException {
    public InvalidPaymentChannelException(String message){
        super(message);
    }
}
