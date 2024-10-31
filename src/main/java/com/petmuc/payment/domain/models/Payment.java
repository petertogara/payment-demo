package com.petmuc.payment.domain.models;


import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String method;
    private BigDecimal amount;

    @ManyToOne
    private Customer customer;

    private LocalDateTime paymentDate;

    private String reference;

    public Payment() {

    }
    public Payment(String method, BigDecimal amount, Customer customer, LocalDateTime paymentDate, String reference) {
        this.method = method;
        this.amount = amount;
        this.customer = customer;
        this.paymentDate = paymentDate;
        this.reference = reference;
    }

    public Payment(String method, BigDecimal amount, Customer customer, String reference) {
        this.method = method;
        this.amount = amount;
        this.customer = customer;
        this.reference = reference;
    }

    public Payment(Long id, String method, BigDecimal amount, Customer customer, String reference) {
        this.id = id;
        this.method = method;
        this.amount = amount;
        this.customer = customer;
        this.reference = reference;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}