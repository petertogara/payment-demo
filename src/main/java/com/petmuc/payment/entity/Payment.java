package com.petmuc.payment.entity;


import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String method;  // Visa, PayPal, etc.
    private BigDecimal amount;

    @ManyToOne
    private Customer customer;

    private LocalDateTime paymentDate;

    public Payment() {}
    public Payment(
            Long id, String method, BigDecimal amount, Customer customer) {
        this.id = id;
        this.method = method;
        this.amount = amount;
        this.customer = customer;
        this.paymentDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Customer getCustomer() { return customer; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}