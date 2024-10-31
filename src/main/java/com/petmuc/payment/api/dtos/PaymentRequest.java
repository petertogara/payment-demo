package com.petmuc.payment.api.dtos;

public record PaymentRequest(String reference, Long customerId, double amount) {
}
