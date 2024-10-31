package com.petmuc.payment.domain.services.impl;

import com.petmuc.payment.domain.models.Customer;
import com.petmuc.payment.domain.models.Payment;
import com.petmuc.payment.domain.repositories.PaymentRepository;
import com.petmuc.payment.exception.CustomerNotFoundException;
import com.petmuc.payment.exception.PaymentNotFoundException;
import com.petmuc.payment.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @InjectMocks
    private CustomerServiceImpl customerService;


    @Nested
    class MakePaymentTests {
        private Long customerId;
        private Long paymentId;
        private Customer customer;
        private Payment payment;
        private String reference;

        @BeforeEach
        void setUp() {
            customerId = 1L;
            paymentId = 2L;
            reference = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
            customer = new Customer(1L, "John Doe", "john.doe@example.com");
            payment = new Payment("VISA", new BigDecimal("12000.00"),customer,reference);
        }

        @Test
        void shouldMakePaymentSuccessfully() {

            Mockito.when(customerService.getCustomerById(customerId)).thenReturn(customer);
            Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            Payment paid = paymentService.makePayment(customerId, payment);

            assertEquals(paid.getAmount(), payment.getAmount());
            assertEquals(paid.getCustomer(), customer);
            Mockito.verify(customerService, Mockito.times(1)).getCustomerById(customerId);
            Mockito.verify(paymentRepository, Mockito.times(1)).save(any(Payment.class));

        }

        @Test
        void shouldThrowCustomerNotFoundExceptionWhenMakingPaymentAndFail() {

            Payment request = payment;
            request.setCustomer(null);
            Mockito.when(customerService.getCustomerById(customerId)).thenReturn(nullable(any()));
            assertThrows(CustomerNotFoundException.class, () -> paymentService.makePayment(customerId, request));
            Mockito.verify(customerService, Mockito.times(1)).getCustomerById(customerId);
            Mockito.verify(paymentRepository, Mockito.times(0)).save(any(Payment.class));
        }

        @Test
        void shouldThrowServiceExceptionWhenMakingPaymentWithInexistentCustomerAndFail() {
            //fails
            Payment request = payment;
            request.setCustomer(null);
            Mockito.when(customerService.getCustomerById(customerId)).thenReturn(customer);
            Mockito.when(paymentRepository.save(any(Payment.class))).thenThrow(DataIntegrityViolationException.class);
            assertThrows(ServiceException.class, () -> paymentService.makePayment(customerId, request));
            Mockito.verify(customerService, Mockito.times(1)).getCustomerById(customerId);
            Mockito.verify(paymentRepository, Mockito.times(1)).save(any(Payment.class));
        }

        @Test
        void shouldFindPaymentsByCustomerIdSuccessfully() {

            List<Payment> payments = Arrays.asList(
                    new Payment(1L,"VISA", new BigDecimal("12000.00"),customer,reference),
                    new Payment(2L,"PayPal", new BigDecimal("5800.90"), customer,reference));

            Mockito.when(paymentRepository.findByCustomerId(customerId)).thenReturn(payments);
            List<Payment> foundPayments = paymentService.getPaymentsByCustomerId(customerId);
            assertEquals(payments.size(), foundPayments.size());
            Mockito.verify(paymentRepository, Mockito.times(1)).findByCustomerId(customerId);
        }

        @Test
        void shouldNotFindPaymentsByCustomerIdWithNoPaymentsLinked() {

            List<Payment> payments = List.of();

            Mockito.when(paymentRepository.findByCustomerId(customerId)).thenReturn(payments);
            List<Payment> foundPayments = paymentService.getPaymentsByCustomerId(customerId);
            assertEquals(0, foundPayments.size());
            Mockito.verify(paymentRepository, Mockito.times(1)).findByCustomerId(customerId);
        }


        @Test
        void shouldFindPaymentByIdSuccessfully() {

            Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
            Payment foundPayment = paymentService.getPaymentById(paymentId);

            assertEquals(foundPayment.getAmount(), payment.getAmount());
            assertEquals(foundPayment.getCustomer(), customer);
            Mockito.verify(paymentRepository, Mockito.times(1)).findById(paymentId);
        }

        @Test
        void shouldThrowPaymentNotFoundExceptionWhenFindingPaymentById() {

            Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
            assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(paymentId));
            Mockito.verify(paymentRepository, Mockito.times(1)).findById(paymentId);
        }
    }

}
