CREATE DATABASE IF NOT EXISTS pharmacy_management;
USE pharmacy_management;

CREATE TABLE IF NOT EXISTS users (
    user_id  INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     ENUM('ADMIN','PHARMACIST') NOT NULL
);

INSERT INTO users (username, password, role) VALUES
('admin',   'admin123', 'ADMIN'),
('pharma1', 'pass123',  'PHARMACIST'),
('pharma2', 'pass456',  'PHARMACIST');
