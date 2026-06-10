DROP TABLE IF EXISTS terminal_requests;

CREATE TABLE terminal_requests (
   id UUID PRIMARY KEY,
   customer_id VARCHAR(100) NOT NULL,
   terminal_type VARCHAR(50) NOT NULL,
   street VARCHAR(255) NOT NULL,
   number VARCHAR(50) NOT NULL,
   city VARCHAR(100) NOT NULL,
   state VARCHAR(2) NOT NULL,
   zip_code VARCHAR(20) NOT NULL,
   status VARCHAR(50) NOT NULL,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NOT NULL
);