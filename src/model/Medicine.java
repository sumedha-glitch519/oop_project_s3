package model;

import java.sql.Date;

/**
 * Represents a row in the "medicines" table.
 * The "manufacturer" field has been removed.
 * "prescriptionRequired" controls whether this medicine can be sold
 * through the "Issue Medicine Without Prescription" counter-sale screen.
 */
public class Medicine {
    private int medicineId;
    private String medicineName;
    private String category;
    private double price;
    private int stockQuantity;
    private Date expiryDate;
    private boolean prescriptionRequired;

    public Medicine() {
    }

    public Medicine(int medicineId, String medicineName, String category,
                     double price, int stockQuantity, Date expiryDate, boolean prescriptionRequired) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.expiryDate = expiryDate;
        this.prescriptionRequired = prescriptionRequired;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isPrescriptionRequired() {
        return prescriptionRequired;
    }

    public void setPrescriptionRequired(boolean prescriptionRequired) {
        this.prescriptionRequired = prescriptionRequired;
    }

    // ==================================================================
    // MERGE: added for login_module (Admin module)
    // ==================================================================
    public Medicine(int id, String name, String category, double price,
                    int quantity, String expiry, boolean prescriptionRequired) {
        this(id, name, category, price, quantity,
                (expiry == null || expiry.isEmpty()) ? null : Date.valueOf(expiry),
                prescriptionRequired);
    }

    public int getId() {
        return medicineId;
    }

    public String getName() {
        return medicineName;
    }

    public int getQuantity() {
        return stockQuantity;
    }

    public String getExpiry() {
        return expiryDate == null ? null : expiryDate.toString();
    }
}
