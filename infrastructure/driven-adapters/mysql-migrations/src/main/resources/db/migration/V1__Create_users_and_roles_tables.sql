CREATE TABLE roles (
role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) UNIQUE NOT NULL,
description VARCHAR(200)
);

-- Crear tabla users
CREATE TABLE users (
user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(100) NOT NULL,
last_name VARCHAR(100) NOT NULL,
email VARCHAR(150) UNIQUE NOT NULL,
identity_document VARCHAR(20) UNIQUE NOT NULL,
phone VARCHAR(15),
role_id BIGINT NOT NULL,
base_salary DECIMAL(15,2) NOT NULL CHECK (base_salary BETWEEN 0 AND 150000000),
password VARCHAR(255) NOT NULL,
creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

INSERT INTO roles (name, description) VALUES
('CUSTOMER', 'Loan applicant user'),
('ADVISOR', 'User who reviews applications'),
('ADMINISTRATOR', 'User with full access');