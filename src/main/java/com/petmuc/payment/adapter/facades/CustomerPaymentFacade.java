package com.petmuc.payment.adapter.facades;

import com.petmuc.payment.domain.models.Customer;
import com.petmuc.payment.domain.models.Payment;

import java.util.List;

public interface CustomerPaymentFacade {
    Customer createCustomer(Customer customer);

    Customer updateCustomer(Long customerId, Customer customer);

    void deleteCustomer(Long customerId);

    Customer getCustomerById(Long customerId);

    List<Customer> getAllCustomers();

    Payment makePayment(Long customerId, Payment payment);

    Payment getPaymentById(Long paymentId);

    List<Payment> getPaymentsByCustomerId(Long customerId);
}
