package com.petmuc.payment.adapter.facades.impl;

import com.petmuc.payment.adapter.facades.CustomerPaymentFacade;
import com.petmuc.payment.domain.models.Customer;
import com.petmuc.payment.domain.models.Payment;
import com.petmuc.payment.domain.services.CustomerService;
import com.petmuc.payment.domain.services.PaymentService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerPaymentFacadeImpl implements CustomerPaymentFacade {
    private final CustomerService customerService;
    private final PaymentService paymentService;

    public CustomerPaymentFacadeImpl(CustomerService customerService, PaymentService paymentService) {
        this.customerService = customerService;
        this.paymentService = paymentService;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return customerService.createCustomer(customer);
    }
    @Override
    public Customer updateCustomer(Long customerId, Customer customer) {
        return customerService.updateCustomer(customerId, customer);
    }
    @Override
    public void deleteCustomer(Long customerId) {
        customerService.deleteCustomer(customerId);
    }
    @Override
    public Customer getCustomerById(Long customerId) {
        return customerService.getCustomerById(customerId);
    }
    @Override
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
    @Override
    public Payment makePayment(Long customerId, Payment payment) {
        return paymentService.makePayment(customerId, payment);
    }

    @Override
    public Payment getPaymentById(Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    @Override
    public List<Payment> getPaymentsByCustomerId(Long customerId) {
        return paymentService.getPaymentsByCustomerId(customerId);
    }
}
