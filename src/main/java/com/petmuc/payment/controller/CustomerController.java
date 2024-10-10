package com.petmuc.payment.controller;

import com.petmuc.payment.entity.Customer;
import com.petmuc.payment.entity.Payment;
import com.petmuc.payment.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return new ResponseEntity<>(customerService.updateCustomer(id, customer), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return new ResponseEntity<>(customerService.findCustomerById(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<Payment>> getPaymentsByCustomerId(@PathVariable Long id) {
        return new ResponseEntity<>(customerService.findPaymentsByCustomerId(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<Payment> makePayment(@PathVariable Long id, @RequestBody Payment payment) {
        return new ResponseEntity<>(customerService.makePayment(id, payment), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> findAllCustomers() {
        return new ResponseEntity<>(customerService.findAllCustomers(),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}

