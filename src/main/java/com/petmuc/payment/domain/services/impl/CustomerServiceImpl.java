package com.petmuc.payment.domain.services.impl;

import com.petmuc.payment.domain.models.Customer;
import com.petmuc.payment.exception.CustomerAlreadyExistsException;
import com.petmuc.payment.exception.CustomerNotFoundException;
import com.petmuc.payment.domain.repositories.CustomerRepository;
import com.petmuc.payment.domain.services.CustomerService;
import com.petmuc.payment.utils.MessageUtil;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final MessageUtil messageUtil;
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(MessageUtil messageUtil, CustomerRepository customerRepository) {
        this.messageUtil = messageUtil;
        this.customerRepository = customerRepository;
    }


    @Override
    @Transactional
    public Customer createCustomer(@Valid Customer customer) {
        checkCustomerExists(customer);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long customerId, @Valid Customer customer) {
        Customer existing = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(messageUtil.getCustomerNotFoundMessage(customerId)));

        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        return customerRepository.save(existing);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(messageUtil.getCustomerNotFoundMessage(customerId));
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(messageUtil.getCustomerNotFoundMessage(customerId)));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    private void checkCustomerExists(Customer customer) {
        boolean exists = customerRepository.findCustomerByEmail(customer.getEmail()).isPresent();
        if (exists) {
            throw new CustomerAlreadyExistsException(messageUtil.getCustomerAlreadyExistsMessage(customer.getEmail()));
        }
    }


}
