package com.petmuc.payment.controller;

import static com.petmuc.payment.service.impl.CustomerServiceImpl.CUSTOMER_ALREADY_EXISTS_MESSAGE;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import com.petmuc.payment.entity.Customer;
import com.petmuc.payment.repository.CustomerRepository;
import com.petmuc.payment.service.impl.CustomerServiceImpl;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIT {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @SpyBean
    private CustomerServiceImpl customerServiceImpl;

    @SpyBean
    private CustomerRepository customerRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        customerRepository.deleteAll();
    }

    @Test
    void shouldGetAllCustomers() {
        List<Customer> customers = List.of(
                new Customer(null, "John Doe", "john@hellomail.com"),
                new Customer(null, "Dennis Taru", "dennis@hellomail.com")
        );
        customerRepository.saveAll(customers);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/customers")
                .then()
                .statusCode(200)
                .body(".", hasSize(2));
    }

    @Test
    void shouldCreateCustomerSuccessfully() {

        Customer customer = new Customer(null, "John Doe", "john.doe@hellomail.com");

        given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when()
                .post("/api/v1/customers")
                .then()
                .log().ifError()
                .statusCode(201)
                .body("name", equalTo("John Doe"))
                .body("email", equalTo("john.doe@hellomail.com"))
                .extract()
                .response();

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerServiceImpl, times(1)).createCustomer(customerCaptor.capture());

        Mockito.verify(customerRepository, times(1)).save(Mockito.any(Customer.class));

        Optional<Customer> savedCustomer = customerRepository.findCustomerByEmail(customerCaptor.getValue().getEmail());
        assertThat(savedCustomer).isPresent();
        assertThat(savedCustomer.get().getName()).isEqualTo(customerCaptor.getValue().getName());
        assertThat(savedCustomer.get().getEmail()).isEqualTo(customerCaptor.getValue().getEmail());

    }

    @Test
    void shouldCreateCustomerThrowCustomerAlreadyExistsException() {

        Customer existingCustomer = new Customer(null, "Jane Doe", "jane@hellomail.com");
        customerRepository.save(existingCustomer);

        Customer customer = new Customer(null, "Jane Doe", "jane@hellomail.com");
        given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when()
                .post("/api/v1/customers")
                .then()
                .log().ifError()
                .statusCode(409)
                .body("detail", equalTo(String.format(CUSTOMER_ALREADY_EXISTS_MESSAGE, customer.getEmail())))
                .extract()
                .response();

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerServiceImpl, times(1)).createCustomer(customerCaptor.capture());
        Mockito.verify(customerRepository, times(1)).findCustomerByEmail(customerCaptor.getValue().getEmail());

        assertThat(customerCaptor.getValue().getEmail()).isEqualTo(customer.getEmail());

    }

    @Test
    void shouldGetCustomerByIdSuccessfully() {

        Customer customer = new Customer(null, "John Doe", "john@hellomail.com");

        Customer savedCustomer = customerRepository.save(customer);
        assertNotNull(savedCustomer.getId());

        given()
                .pathParam("id", savedCustomer.getId())
                .when()
                .get("/api/v1/customers/{id}")
                .then()
                .log().ifError()
                .statusCode(200)
                .body("id", equalTo(savedCustomer.getId().intValue()))
                .body("name", equalTo("John Doe"))
                .body("email", equalTo("john@hellomail.com"))
                .extract()
                .response();

        Mockito.verify(customerServiceImpl, times(1)).findCustomerById(savedCustomer.getId());
        Mockito.verify(customerRepository, times(1)).findById(savedCustomer.getId());

        Optional<Customer> existingCustomer = customerRepository.findById(savedCustomer.getId());

        assertThat(existingCustomer).isPresent();
        assertThat(existingCustomer.get().getEmail()).isEqualTo(savedCustomer.getEmail());
        assertThat(existingCustomer.get().getName()).isEqualTo(savedCustomer.getName());

    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        Customer customer = new Customer(null, "John Doe", "john@hellomail.com");
        Customer updatedCustomer = new Customer(null, "Jane Dorris", "jane@hellomail.com");
        Customer savedCustomer = customerRepository.save(customer);
        assertNotNull(savedCustomer);

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", savedCustomer.getId())
                .body(updatedCustomer)
                .when()
                .put("/api/v1/customers/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(savedCustomer.getId().intValue()))
                .body("name", equalTo(updatedCustomer.getName()))
                .body("email", equalTo(updatedCustomer.getEmail()))
                .extract()
                .response();

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerServiceImpl, times(1)).updateCustomer(eq(savedCustomer.getId().longValue()), customerCaptor.capture());
        Mockito.verify(customerRepository, times(1)).findById(any());
        Mockito.verify(customerRepository, times(2)).save(customerCaptor.capture());

        Customer capturedCustomer = customerCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo("Jane Dorris");
        assertThat(capturedCustomer.getEmail()).isEqualTo("jane@hellomail.com");

    }


}