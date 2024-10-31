package com.petmuc.payment.domain.services;

import com.petmuc.payment.domain.models.Customer;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Long customerId, Customer customer);

    Customer getCustomerById(Long customerId);

    List<Customer> getAllCustomers();

    void deleteCustomer(Long customerId);
}
