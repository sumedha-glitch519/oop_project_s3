-- ============================================================
-- Pharmacy Management System - Database Schema (v2)
-- ============================================================

DROP DATABASE IF EXISTS pharmacy_management;
CREATE DATABASE pharmacy_management;
USE pharmacy_management;

-- ------------------------------------------------------------
-- Table: users
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(50) NOT NULL,
    role        VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'PHARMACIST'))
);

-- ------------------------------------------------------------
-- Table: customers
-- date_of_birth / gender replace the old date_registered field.
-- ------------------------------------------------------------
CREATE TABLE customers (
    customer_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    phone           VARCHAR(15) NOT NULL,
    email           VARCHAR(100),
    address         VARCHAR(255),
    date_of_birth   DATE,
    gender          VARCHAR(10)
);

-- ------------------------------------------------------------
-- Table: medicines
-- manufacturer column removed; prescription_required added.
-- ------------------------------------------------------------
CREATE TABLE medicines (
    medicine_id             INT AUTO_INCREMENT PRIMARY KEY,
    medicine_name           VARCHAR(100) NOT NULL,
    category                VARCHAR(50),
    price                   DECIMAL(10,2) NOT NULL,
    stock_quantity          INT NOT NULL DEFAULT 0,
    expiry_date             DATE,
    prescription_required   BOOLEAN NOT NULL DEFAULT FALSE
);

-- ------------------------------------------------------------
-- Table: prescriptions
-- pharmacist_id and total_amount removed - a prescription is now
-- just "what the doctor/pharmacist noted down for this customer".
-- The pharmacist and the money total live on the bills table instead.
-- ------------------------------------------------------------
CREATE TABLE prescriptions (
    prescription_id   INT AUTO_INCREMENT PRIMARY KEY,
    customer_id       INT NOT NULL,
    prescription_date DATETIME NOT NULL,
    CONSTRAINT fk_prescription_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- ------------------------------------------------------------
-- Table: prescription_medicines
-- The medicines noted on a prescription (before any money changes hands).
-- ------------------------------------------------------------
CREATE TABLE prescription_medicines (
    prescription_medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id          INT NOT NULL,
    medicine_id              INT NOT NULL,
    quantity                 INT NOT NULL,
    unit_price                DECIMAL(10,2) NOT NULL,
    total_price               DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_pm_prescription
        FOREIGN KEY (prescription_id) REFERENCES prescriptions(prescription_id),
    CONSTRAINT fk_pm_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
);

-- ------------------------------------------------------------
-- Table: bills
-- ONE common bill table for both prescription sales and
-- walk-in (non-prescription) counter sales.
-- prescription_id is NULL for a non-prescription sale.
-- ------------------------------------------------------------
CREATE TABLE bills (
    bill_id          INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id  INT NULL,
    pharmacist_id    INT NOT NULL,
    bill_date        DATETIME DEFAULT CURRENT_TIMESTAMP,
    bill_amount      DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_bill_prescription
        FOREIGN KEY (prescription_id) REFERENCES prescriptions(prescription_id),
    CONSTRAINT fk_bill_pharmacist
        FOREIGN KEY (pharmacist_id) REFERENCES users(user_id)
);

-- ------------------------------------------------------------
-- Table: bill_items
-- Line items for EVERY bill (prescription-based or walk-in).
-- ------------------------------------------------------------
CREATE TABLE bill_items (
    bill_item_id  INT AUTO_INCREMENT PRIMARY KEY,
    bill_id       INT NOT NULL,
    medicine_id   INT NOT NULL,
    quantity      INT NOT NULL,
    unit_price    DECIMAL(10,2) NOT NULL,
    total_price   DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_bi_bill
        FOREIGN KEY (bill_id) REFERENCES bills(bill_id),
    CONSTRAINT fk_bi_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Users: 1 admin, 2 pharmacists
INSERT INTO users (username, password, role) VALUES
('admin1', 'admin123', 'ADMIN'),
('pharma1', 'pharma123', 'PHARMACIST'),
('pharma2', 'pharma456', 'PHARMACIST');

-- Customers: at least 3
INSERT INTO customers (name, phone, email, address, date_of_birth, gender) VALUES
('Ravi Kumar', '9876543210', 'ravi.kumar@example.com', 'MG Road, Kochi', '1990-04-12', 'Male'),
('Anjali Nair', '9123456780', 'anjali.nair@example.com', 'Panampilly Nagar, Kochi', '1995-09-23', 'Female'),
('Suresh Menon', '9988776655', 'suresh.menon@example.com', 'Edappally, Kochi', '1982-01-30', 'Male');

-- Medicines: mix of prescription_required true/false, low stock,
-- expired, and expiring-soon items so every reminder status can be demoed.
INSERT INTO medicines (medicine_name, category, price, stock_quantity, expiry_date, prescription_required) VALUES
('Paracetamol 500mg', 'Analgesic', 20.00, 150, '2027-06-30', FALSE),
('Amoxicillin 250mg', 'Antibiotic', 45.50, 8,   '2026-12-31', TRUE),
('Cetirizine 10mg', 'Antihistamine', 15.00, 200, '2027-03-31', FALSE),
('Ibuprofen 400mg', 'Analgesic', 25.00, 5,   '2026-10-15', FALSE),
('Metformin 500mg', 'Antidiabetic', 30.00, 120, '2027-08-31', TRUE),
('Omeprazole 20mg', 'Antacid', 40.00, 9,   '2026-10-20', FALSE),
('Azithromycin 500mg', 'Antibiotic', 60.00, 60,  '2026-09-15', TRUE),
('Vitamin C 500mg', 'Supplement', 18.00, 3,   '2027-01-31', FALSE),
('Cough Syrup 100ml', 'Cough Relief', 55.00, 75,  '2026-12-15', FALSE),
('Insulin Injection', 'Antidiabetic', 250.00, 40,  '2026-10-05', TRUE);

-- Note: run this script close to October 2026 for the "EXPIRED" /
-- "EXPIRING SOON" sample rows above to display meaningfully; adjust
-- the expiry_date values if you run it much later.
