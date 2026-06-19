DROP TABLE IF EXISTS terminal_request_errors;
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
   external_key VARCHAR(255) UNIQUE,
   status VARCHAR(50) NOT NULL,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NOT NULL,
   version BIGINT
);

CREATE TABLE terminal_request_errors (
   id UUID PRIMARY KEY,
   terminal_request_id UUID NOT NULL UNIQUE,
   last_error VARCHAR(1000) NOT NULL,
   retry_count INTEGER NOT NULL,
   last_attempt_at TIMESTAMP NOT NULL,
   retry_pending BOOLEAN NOT NULL,
   CONSTRAINT fk_terminal_request_errors_terminal_request
       FOREIGN KEY (terminal_request_id) REFERENCES terminal_requests(id)
);
