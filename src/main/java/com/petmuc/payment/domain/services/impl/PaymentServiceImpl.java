package com.petmuc.payment.domain.services.impl;

import com.petmuc.payment.adapter.clients.RestClient;
import com.petmuc.payment.api.dtos.*;
import com.petmuc.payment.domain.models.Customer;
import com.petmuc.payment.domain.models.Payment;
import com.petmuc.payment.domain.repositories.PaymentRepository;
import com.petmuc.payment.domain.services.CustomerService;
import com.petmuc.payment.domain.services.PaymentService;
import com.petmuc.payment.exception.PaymentNotFoundException;
import com.petmuc.payment.exception.ProcessType;
import com.petmuc.payment.utils.MessageUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final MessageUtil messageUtil;
    private final RestClient restClient;
    private final CustomerService customerService;
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(MessageUtil messageUtil, RestClient restClient, CustomerService customerService, PaymentRepository paymentRepository) {
        this.messageUtil = messageUtil;
        this.restClient = restClient;
        this.customerService = customerService;
        this.paymentRepository = paymentRepository;

    }

    @Override
    @Transactional
    public Payment makePayment(Long customerId, @Valid Payment payment) {
        final String reference = UUID.randomUUID().toString();
        Customer customer = customerService.getCustomerById(customerId);
        payment.setCustomer(customer);
        payment.setReference(reference);

        processPayment(reference,customerId, payment.getAmount());

        return paymentRepository.save(payment);

    }

    @Override
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(messageUtil.getPaymentNotFoundMessage(paymentId)));
    }

    @Override
    public List<Payment> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    private void processPayment(String reference, Long customerId, BigDecimal amount) {
        PaymentRequest request = new PaymentRequest(reference, customerId, amount.doubleValue());
        ResponseEntity<PaymentResponse> responseEntity = restClient.payment(request);
        validateResponse(responseEntity, reference, ProcessType.PAYMENT);
    }

    private void processPaymentReversal(String reference, BigDecimal amount) {
        ReversalRequest request = new ReversalRequest(reference,amount.doubleValue());
        ResponseEntity<ReversalResponse> responseEntity = restClient.paymentReversal(request);
        validateResponse(responseEntity, reference, ProcessType.REVERSAL);
    }

    private <T> void validateResponse(ResponseEntity<T> responseEntity, String reference, ProcessType processType) {
        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            return;
        }

        final String errorMessage = responseEntity.getBody() != null
                ? ((ErrorResponse) responseEntity.getBody()).message()
                : messageUtil.getDefaultErrorMessage();

        processType.handleError(reference, errorMessage, messageUtil);
    }
}
