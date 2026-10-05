package medicine;

public class Medicine {

    // Medicine details
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private String expiryDate;
    private boolean prescriptionRequired;

    // Constructor
    public Medicine(int id, String name, String category, double price,
            int quantity, String expiryDate,
            boolean prescriptionRequired) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.prescriptionRequired = prescriptionRequired;
    }

    // Get ID
    public int getId() {
        return id;
    }

    // Get name
    public String getName() {
        return name;
    }

    // Get category
    public String getCategory() {
        return category;
    }

    // Get price
    public double getPrice() {
        return price;
    }

    // Get quantity
    public int getQuantity() {
        return quantity;
    }

    // Get expiry date
    public String getExpiryDate() {
        return expiryDate;
    }

    // Check whether prescription is required
    public boolean isPrescriptionRequired() {
        return prescriptionRequired;
    }

    // Set name
    public void setName(String name) {
        this.name = name;
    }

    // Set category
    public void setCategory(String category) {
        this.category = category;
    }

    // Set price
    public void setPrice(double price) {
        this.price = price;
    }

    // Set quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Set expiry date
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Set prescription requirement
    public void setPrescriptionRequired(boolean prescriptionRequired) {
        this.prescriptionRequired = prescriptionRequired;
    }
}