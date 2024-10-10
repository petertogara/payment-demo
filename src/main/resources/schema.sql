DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE
);
CREATE TABLE payments (
                         id SERIAL PRIMARY KEY,
                         method VARCHAR(50) NOT NULL,
                         amount DECIMAL(10, 2) NOT NULL,
                         customer_id BIGINT NOT NULL,
                         FOREIGN KEY (customer_id) REFERENCES customers(id)
);
