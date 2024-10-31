package com.petmuc.payment.domain.services.impl;

import com.petmuc.payment.domain.models.Customer;
import com.petmuc.payment.exception.CustomerAlreadyExistsException;
import com.petmuc.payment.exception.CustomerNotFoundException;
import com.petmuc.payment.domain.repositories.CustomerRepository;
import com.petmuc.payment.domain.repositories.PaymentRepository;
import com.petmuc.payment.utils.MessageUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private MessageUtil messageUtil;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer, existingCustomer;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer(1L, "John Doe", "john.doe@example.com");
        existingCustomer = new Customer(1L, "John Doe", "john.doe@example.com");
    }

    @Nested
    class CreateCustomerTests {

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
            Mockito.when(messageUtil.getCustomerAlreadyExistsMessage(customer.getEmail()))
                    .thenReturn("Customer not found with id: " + customer.getEmail());

            CustomerAlreadyExistsException thrownException = assertThrows(CustomerAlreadyExistsException.class, () -> customerService.createCustomer(customer));

            Mockito.verify(customerRepository, Mockito.times(1)).findCustomerByEmail(customer.getEmail());
            assertEquals(messageUtil.getCustomerAlreadyExistsMessage(customer.getEmail()), thrownException.getMessage());
            Mockito.verify(customerRepository, Mockito.times(0)).save(any(Customer.class));
        }

    }

    @Nested
    class UpdateCustomerTests {

        @Test
        void shouldUpdateCustomerSuccessfully() {

            Customer updatedCustomer = new Customer(1L, "Janet Doe", "janet.doe@example.com");

            Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
            Mockito.when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

            ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

            Customer savedCustomer = customerService.updateCustomer(1L, updatedCustomer);

            Mockito.verify(customerRepository, Mockito.times(1)).findById(1L);

            Mockito.verify(customerRepository, Mockito.times(1)).save(customerCaptor.capture());

            Customer capturedCustomer = customerCaptor.getValue();
            assertEquals("Janet Doe", capturedCustomer.getName());
            assertEquals("janet.doe@example.com", capturedCustomer.getEmail());

            assertEquals(savedCustomer.getName(), capturedCustomer.getName());
            assertEquals(savedCustomer.getEmail(), capturedCustomer.getEmail());

        }

    }

    @Nested
    class FindCustomerTests {

        @Test
        void shouldThrowCustomerNotFoundExceptionWhenFindingCustomerById() {

            Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.empty());
            Mockito.when(messageUtil.getCustomerNotFoundMessage(1L))
                    .thenReturn("Customer not found with id: " + customer.getId());
            assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(1L, customer));
            Mockito.verify(customerRepository, Mockito.times(1)).findById(1L);

        }

        @Test
        void shouldFindCustomerByIdSuccessfully() {

            Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            Customer foundCustomer = customerService.getCustomerById(1L);
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
            List<Customer> foundCustomers = customerService.getAllCustomers();
            assertEquals(customers.size(), foundCustomers.size());
            Mockito.verify(customerRepository, Mockito.times(1)).findAll();

        }

    }

    @Nested
    class DeleteCustomerTests {

        @Test
        void shouldDeleteCustomerSuccessfully() {
            customer = new Customer(1L, "John Doe", "john.doe@example.com");
            Mockito.when(customerRepository.existsById(1L)).thenReturn(true);
            customerService.deleteCustomer(1L);
            Mockito.verify(customerRepository, Mockito.times(1)).deleteById(1L);
        }

        @Test
        void shouldThrowCustomerNotFoundExceptionWhenDeletingCustomer() {

            Mockito.when(customerRepository.existsById(1L)).thenReturn(false);
            Mockito.when(messageUtil.getCustomerNotFoundMessage(1L))
                            .thenReturn("Customer not found with id: " + 1L);
            assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(1L));
            Mockito.verify(customerRepository, Mockito.times(1)).existsById(1L);
        }
    }
}
