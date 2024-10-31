package com.petmuc.payment.api.dtos;

public record ReversalRequest(String reference, double amount) {
}
