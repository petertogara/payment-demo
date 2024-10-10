package com.petmuc.payment.service.impl;

import com.petmuc.payment.entity.Customer;
import com.petmuc.payment.entity.Payment;
import com.petmuc.payment.exception.CustomerAlreadyExistsException;
import com.petmuc.payment.exception.CustomerNotFoundException;
import com.petmuc.payment.exception.PaymentNotFoundException;
import com.petmuc.payment.exception.ServiceException;
import com.petmuc.payment.repository.CustomerRepository;
import com.petmuc.payment.repository.PaymentRepository;
import org.hibernate.exception.ConstraintViolationException;
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

import static com.petmuc.payment.service.impl.CustomerServiceImpl.CUSTOMER_ALREADY_EXISTS_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private CustomerServiceImpl customerService;

    @Nested
    class CreateCustomerTests {

        private Customer customer;

        @BeforeEach
        void setUp() {
            customer = new Customer(1L, "John Doe", "john.doe@example.com");
        }

        @Test
        void shouldCreateCustomerSuccessfully() {

            Mockito.when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            Customer createdCustomer = customerService.createCustomer(customer);

            assertEquals(customer.getName(), createdCustomer.getName());
        }

        @Test
        void shouldThrowGenericExceptionAndFailWhenCreatingCustomer() {

            Mockito.when(customerRepository.save(any(Customer.class))).thenThrow(ConstraintViolationException.class);
            assertThrows(Exception.class, () -> customerService.createCustomer(customer));

        }

        @Test
        void shouldThrowCustomerAlreadyExistsExceptionWhenCreatingCustomer() {

            customer = new Customer(null, "John Doe", "john.doe@example.com");
            Mockito.when(customerRepository.findCustomerByEmail(customer.getEmail()))
                    .thenReturn(Optional.of(customer));

            CustomerAlreadyExistsException thrownException = assertThrows(CustomerAlreadyExistsException.class, () -> customerService.createCustomer(customer));

            Mockito.verify(customerRepository, Mockito.times(1)).findCustomerByEmail(customer.getEmail());
            assertEquals(String.format(CUSTOMER_ALREADY_EXISTS_MESSAGE,customer.getEmail()), thrownException.getMessage());
            Mockito.verify(customerRepository, Mockito.times(0)).save(any(Customer.class));
        }

    }

    @Nested
    class UpdateCustomerTests {
        private Customer existingCustomer;

        @BeforeEach
        void setUp() {
            existingCustomer = new Customer(1L, "John Doe", "john.doe@example.com");
        }

        @Test
        void shouldUpdateCustomerSuccessfully() {

            Customer updatedCustomer = new Customer(1L, "Janet Doe", "janet.doe@example.com");

            Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
            Mockito.when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
            Customer savedCustomer = customerService.updateCustomer(1L, existingCustomer);

            assertEquals(updatedCustomer.getName(), savedCustomer.getName());
            assertEquals(updatedCustomer.getEmail(), savedCustomer.getEmail());
            Mockito.verify(customerRepository, Mockito.times(1)).findById(1L);
            Mockito.verify(customerRepository, Mockito.times(1)).save(any(Customer.class));


        }

    }

    @Nested
    class FindCustomerTests {
        private Customer customer;

        @BeforeEach
        void setUp() {
            customer = new Customer(1L, "John Doe", "john.doe@example.com");
        }

        @Test
        void shouldThrowCustomerNotFoundExceptionWhenFindingCustomerById() {

            Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(1L, customer));
            Mockito.verify(customerRepository, Mockito.times(1)).findById(1L);

        }

        @Test
        void shouldFindCustomerByIdSuccessfully() {

            Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            Customer foundCustomer = customerService.findCustomerById(1L);
            assertEquals(customer.getName(), foundCustomer.getName());
            assertEquals(customer.getEmail(), foundCustomer.getEmail());
            Mockito.verify(customerRepository, Mockito.times(1)).findById(1L);
        }

        @Test
        void shouldFindAllCustomerSuccessfully() {

            List<Customer> customers = Arrays.asList(new Customer(1L, "John Doe", "john.doe@example.com"),
                    new Customer(2L, "Sam Chinogiya", "sam.chinogiya@example.com"),
                    new Customer(3L, "Mukatimarwo Musarurwa", "mukatimarwo.musarurwa@example.com"),
                    new Customer(4L, "Remekedzai Chinyama", "remekedzai.chinyama@example.com"));

            Mockito.when(customerRepository.findAll()).thenReturn(customers);
            List<Customer> foundCustomers = customerService.findAllCustomers();
            assertEquals(customers.size(), foundCustomers.size());
            Mockito.verify(customerRepository, Mockito.times(1)).findAll();

        }

    }

    @Nested
    class DeleteCustomerTests {
        private Long customerId;

        @BeforeEach
        void setUp() {
            customerId = 1L;
        }

        @Test
        void shouldDeleteCustomerSuccessfully() {

            Mockito.when(customerRepository.existsById(customerId)).thenReturn(true);
            customerService.deleteCustomer(customerId);
            Mockito.verify(customerRepository, Mockito.times(1)).deleteById(customerId);
        }

        @Test
        void shouldThrowCustomerNotFoundExceptionWhenDeletingCustomer() {

            Mockito.when(customerRepository.existsById(customerId)).thenReturn(false);
            assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(customerId));
            Mockito.verify(customerRepository, Mockito.times(1)).existsById(customerId);
        }
    }

    @Nested
    class MakePaymentTests {
        private Long customerId;
        private Long paymentId;
        private Customer customer;
        private Payment payment;

        @BeforeEach
        void setUp() {
            customerId = 1L;
            paymentId = 2L;
            customer = new Customer(1L, "John Doe", "john.doe@example.com");
            payment = new Payment(2L, "VISA", new BigDecimal("12000.00"), customer);
        }

        @Test
        void shouldMakePaymentSuccessfully() {

            Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            Payment paid = customerService.makePayment(customerId, payment);

            assertEquals(paid.getAmount(), payment.getAmount());
            assertEquals(paid.getCustomer(), customer);
            Mockito.verify(customerRepository, Mockito.times(1)).findById(customerId);
            Mockito.verify(paymentRepository, Mockito.times(1)).save(any(Payment.class));

        }

        @Test
        void shouldThrowCustomerNotFoundExceptionWhenMakingPaymentAndFail() {

            Payment request = payment;
            request.setCustomer(null);
            Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
            assertThrows(CustomerNotFoundException.class, () -> customerService.makePayment(customerId, request));
            Mockito.verify(customerRepository, Mockito.times(1)).findById(customerId);
            Mockito.verify(paymentRepository, Mockito.times(0)).save(any(Payment.class));
        }

        @Test
        void shouldThrowServiceExceptionWhenMakingPaymentWithInexistentCustomerAndFail() {
            Payment request = payment;
            request.setCustomer(null);
            Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            Mockito.when(paymentRepository.save(any(Payment.class))).thenThrow(DataIntegrityViolationException.class);
            assertThrows(ServiceException.class, () -> customerService.makePayment(customerId, request));
            Mockito.verify(customerRepository, Mockito.times(1)).findById(customerId);
            Mockito.verify(paymentRepository, Mockito.times(1)).save(any(Payment.class));
        }

        @Test
        void shouldFindPaymentsByCustomerIdSuccessfully() {

            List<Payment> payments = Arrays.asList(new Payment(1L, "VISA", new BigDecimal("12000.00"), customer),
                    new Payment(2L, "PayPal", new BigDecimal("5800.90"), customer));

            Mockito.when(paymentRepository.findByCustomerId(customerId)).thenReturn(payments);
            List<Payment> foundPayments = customerService.findPaymentsByCustomerId(customerId);
            assertEquals(payments.size(), foundPayments.size());
            Mockito.verify(paymentRepository, Mockito.times(1)).findByCustomerId(customerId);
        }

        @Test
        void shouldNotFindPaymentsByCustomerIdWithNoPaymentsLinked() {

            List<Payment> payments = List.of();

            Mockito.when(paymentRepository.findByCustomerId(customerId)).thenReturn(payments);
            List<Payment> foundPayments = customerService.findPaymentsByCustomerId(customerId);
            assertEquals(0, foundPayments.size());
            Mockito.verify(paymentRepository, Mockito.times(1)).findByCustomerId(customerId);
        }


        @Test
        void shouldFindPaymentByIdSuccessfully() {

            Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
            Payment foundPayment = customerService.findPaymentById(paymentId);

            assertEquals(foundPayment.getAmount(), payment.getAmount());
            assertEquals(foundPayment.getCustomer(), customer);
            Mockito.verify(paymentRepository, Mockito.times(1)).findById(paymentId);
        }

        @Test
        void shouldThrowPaymentNotFoundExceptionWhenFindingPaymentById() {

            Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
            assertThrows(PaymentNotFoundException.class, () -> customerService.findPaymentById(paymentId));
            Mockito.verify(paymentRepository, Mockito.times(1)).findById(paymentId);
        }
    }
}
