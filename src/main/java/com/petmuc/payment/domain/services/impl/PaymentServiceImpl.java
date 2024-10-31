package com.petmuc.payment.domain.services.impl;

import com.petmuc.payment.adapter.clients.RestClient;
import com.petmuc.payment.api.dtos.PaymentRequest;
import com.petmuc.payment.api.dtos.PaymentResponse;
import com.petmuc.payment.api.dtos.ReversalRequest;
import com.petmuc.payment.api.dtos.ReversalResponse;
import com.petmuc.payment.domain.models.Customer;
import com.petmuc.payment.domain.models.Payment;
import com.petmuc.payment.domain.repositories.PaymentRepository;
import com.petmuc.payment.domain.services.CustomerService;
import com.petmuc.payment.domain.services.PaymentService;
import com.petmuc.payment.exception.PaymentNotFoundException;
import com.petmuc.payment.exception.PaymentProcessingException;
import com.petmuc.payment.exception.ReversalProcessingException;
import com.petmuc.payment.utils.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
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
        validatePaymentResponse(responseEntity);
    }

    private void validatePaymentResponse(ResponseEntity<PaymentResponse> responseEntity) {
        if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
            String errorMessage = responseEntity.getBody() != null ? responseEntity.getBody().message() : "Unknown error occurred.";
            throw new PaymentProcessingException(messageUtil.getPaymentProcessingErrorMessage(errorMessage));
        }
    }

    private void processPaymentReversal(String reference, BigDecimal amount) {
        ReversalRequest request = new ReversalRequest(reference,amount.doubleValue());
        ResponseEntity<ReversalResponse> responseEntity = restClient.paymentReversal(request);
        validateReversalResponse(responseEntity);
    }

    private void validateReversalResponse(ResponseEntity<ReversalResponse> responseEntity) {
        if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
            String errorMessage = responseEntity.getBody() != null ? responseEntity.getBody().message() : "Unknown error occurred.";
            throw new ReversalProcessingException(messageUtil.getReversalProcessingErrorMessage(errorMessage));
        }
    }

}
