package com.petmuc.payment.service;

import com.petmuc.payment.entity.Customer;
import com.petmuc.payment.entity.Payment;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Long id, Customer customer);

    Customer findCustomerById(Long id);

    List<Customer> findAllCustomers();

    Payment findPaymentById(Long id);

    Payment makePayment(Long id, Payment payment);

    List<Payment> findPaymentsByCustomerId(Long customerId);

    void deleteCustomer(Long id);
}
