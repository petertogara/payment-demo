package com.petmuc.payment.api.controllers;

import com.petmuc.payment.adapter.facades.CustomerPaymentFacade;
import com.petmuc.payment.domain.models.Customer;
import com.petmuc.payment.domain.models.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerPaymentFacade facade;

    public CustomerController(CustomerPaymentFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer and returns the created customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
            @ApiResponse(responseCode = "409", description = "Customer with the same email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        return new ResponseEntity<>(facade.createCustomer(customer), HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update an existing customer", description = "Updates the details of an existing customer identified by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Customer> updateCustomer(
            @Parameter(description = "ID of the customer") @PathVariable Long customerId,
            @RequestBody Customer customer) {
        return new ResponseEntity<>(facade.updateCustomer(customerId, customer), HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get a customer by ID", description = "Retrieve a customer by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Customer> getCustomerById(
            @Parameter(description = "ID of the customer") @PathVariable Long customerId) {
        return new ResponseEntity<>(facade.getCustomerById(customerId), HttpStatus.OK);
    }

    @GetMapping("/{customerId}/payments")
    @Operation(summary = "Get payments by customer ID", description = "Retrieve all payments associated with a customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of payments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<List<Payment>> getPaymentsByCustomerId(
            @Parameter(description = "ID of the customer") @PathVariable Long customerId) {
        return new ResponseEntity<>(facade.getPaymentsByCustomerId(customerId), HttpStatus.OK);
    }

    @PostMapping("/{customerId}/payments")
    @Operation(summary = "Make a payment for a customer", description = "Creates a new payment for a customer identified by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Payment> makePayment(
            @Parameter(description = "ID of the customer") @PathVariable Long customerId,
            @RequestBody Payment payment) {
        return new ResponseEntity<>(facade.makePayment(customerId, payment), HttpStatus.CREATED);
    }

    @GetMapping("/payments/{paymentId}")
    @Operation(summary = "Get a payment by ID", description = "Retrieve a payment by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<Payment> getPaymentById(
            @Parameter(description = "ID of the payment") @PathVariable Long paymentId) {
        return new ResponseEntity<>(facade.getPaymentById(paymentId), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of customers retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Customer>> findAllCustomers() {
        return new ResponseEntity<>(facade.getAllCustomers(), HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete a customer by ID", description = "Deletes a customer identified by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID of the customer") @PathVariable Long customerId) {
        facade.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}

