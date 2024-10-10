package com.petmuc.payment.service.impl;

import com.petmuc.payment.entity.Customer;
import com.petmuc.payment.entity.Payment;
import com.petmuc.payment.exception.CustomerAlreadyExistsException;
import com.petmuc.payment.exception.CustomerNotFoundException;
import com.petmuc.payment.exception.PaymentNotFoundException;
import com.petmuc.payment.exception.ServiceException;
import com.petmuc.payment.repository.CustomerRepository;
import com.petmuc.payment.repository.PaymentRepository;
import com.petmuc.payment.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Customer createCustomer(@Valid Customer customer) {
        checkCustomerExists(customer);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, @Valid Customer customer) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));

        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        return customerRepository.save(existing);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
    }

    @Override
    @Transactional
    public Payment makePayment(Long customerId, @Valid Payment payment) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));

        payment.setCustomer(customer);

        try {
            return paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException("An error occurred while saving the payment: " + e.getMessage());
        }


    }

    @Override
    public List<Payment> findPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    private void checkCustomerExists(Customer customer) {
        boolean exists = customerRepository.findCustomerByEmail(customer.getEmail()).isPresent();
        if (exists) {
            throw new CustomerAlreadyExistsException(String.format("Customer with this email %s already exists", customer.getEmail()));
        }
    }

}
