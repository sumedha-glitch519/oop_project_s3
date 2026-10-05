-- ============================================================
-- merge_compat.sql   (run AFTER schema.sql, every time schema.sql is run)
--
-- The Admin "Billing Reports" screen reads prescriptions.pharmacist_id,
-- a column the current schema.sql no longer has (the pharmacist now lives
-- on bills.pharmacist_id). Rather than editing the report code, this script
-- adds the column back and keeps it filled automatically.
-- ============================================================
USE pharmacy_management;

ALTER TABLE prescriptions ADD COLUMN pharmacist_id INT NULL;

DROP TRIGGER IF EXISTS trg_bills_set_prescription_pharmacist;

DELIMITER //
CREATE TRIGGER trg_bills_set_prescription_pharmacist
AFTER INSERT ON bills
FOR EACH ROW
BEGIN
    IF NEW.prescription_id IS NOT NULL THEN
        UPDATE prescriptions
           SET pharmacist_id = NEW.pharmacist_id
         WHERE prescription_id = NEW.prescription_id;
    END IF;
END//
DELIMITER ;

-- Back-fill any bills that already exist
UPDATE prescriptions p
JOIN bills b ON b.prescription_id = p.prescription_id
SET p.pharmacist_id = b.pharmacist_id;
