USE medicine_management;

DROP TABLE IF EXISTS medicine;

CREATE TABLE medicine (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    expiry_date DATE NOT NULL,
    prescription_required BOOLEAN NOT NULL
);

SHOW TABLES;

DESCRIBE medicine;

DESCRIBE medicine;

USE medicine_management;

SELECT * FROM medicine;

USE medicine_management;

DELETE FROM medicine WHERE id = 101;

SELECT * FROM medicine;

SELECT * FROM medicine;

SHOW TABLES;

DESCRIBE medicine;