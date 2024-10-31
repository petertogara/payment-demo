package com.petmuc.payment.exception;

public class ReversalProcessingException extends RuntimeException {
    public ReversalProcessingException(String message) {
        super(message);
    }
}
