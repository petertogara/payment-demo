package com.petmuc.payment.api.dtos;

public record ReversalResponse(String reversalId, String reference, boolean status, double amount, String message) {
}
