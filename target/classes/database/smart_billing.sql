-- DROP DATABASE IF EXISTS smart_billing_db;

CREATE DATABASE smart_billing_db
    WITH
    OWNER = developer
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;


CREATE TABLE login_user (
	id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    user_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    auth_provider VARCHAR(20) DEFAULT 'local',
    google_id VARCHAR(255) UNIQUE,
    role_user VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);


CREATE TABLE customer (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tax_id VARCHAR(50) UNIQUE NOT NULL,
    name_customer VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20),
    risk_score DECIMAL(5, 2) DEFAULT 0.0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contract (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    title VARCHAR(100),
    start_date DATE NOT NULL,
    end_date DATE,
    monthly_fee DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) 
        REFERENCES customer(id) ON DELETE CASCADE
);


CREATE TABLE invoice (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contract_id INTEGER NOT NULL,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    issue_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP NOT NULL,
    total_amount DECIMAL(15, 2) NOT NULL,
    penalty_amount DECIMAL(15, 2) DEFAULT 0.0,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contract FOREIGN KEY (contract_id) 
        REFERENCES contract(id) ON DELETE CASCADE
);


CREATE TABLE payment (
     id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    invoice_id INTEGER NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount_paid DECIMAL(15, 2) NOT NULL,
    payment_method VARCHAR(50),
    reference_number VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice FOREIGN KEY (invoice_id) 
        REFERENCES invoices(id) ON DELETE CASCADE
);

/* DATA */
INSERT INTO login_user
(email, password_hash, user_name, last_name, auth_provider, google_id, role_user, created_at, updated_at, last_login)
VALUES(1, 'anakin@java.com', '$2a$10$YmCAQoEMWek2G.HgqZAMoejxjGAfMsm0gxxiRKdbi1qXoOIvJ0njS', 'Anakin', 'Skywalker', 'LOCAL', NULL, 'ROLE_ADMIN', '2026-05-08 14:54:58.952', '2026-05-08 14:54:58.952', '2026-05-08 14:54:58.947')
;

INSERT INTO login_user
(email, password_hash, user_name, last_name, auth_provider, google_id, role_user, created_at, updated_at, last_login)
VALUES(2, 'obiwan_kenobi@spring.com', '$2a$10$o23wGhzcCspyBe0MVTb6bepW1yUs53LyL8k0o52gF5ctTJ2EJPE4q', 'Obiwan', 'Kenobi', 'LOCAL', NULL, 'ROLE_USER', '2026-05-08 14:59:05.977', '2026-05-08 14:59:05.977', '2026-05-08 14:59:05.976');

INSERT INTO contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(1, 'Service Contract 1', '2026-05-01', '2027-05-01', 1500.00, 'USD', 'ACTIVE', '2026-05-09 16:02:15.972', '2026-12-09 16:02:15.972');
