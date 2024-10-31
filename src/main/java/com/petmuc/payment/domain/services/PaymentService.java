package com.petmuc.payment.domain.services;

import com.petmuc.payment.domain.models.Payment;

import java.util.List;

public interface PaymentService {
    Payment makePayment(Long customerId, Payment payment);

    Payment getPaymentById(Long paymentId);

    List<Payment> getPaymentsByCustomerId(Long customerId);
}
