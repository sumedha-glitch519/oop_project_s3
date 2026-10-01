package model;

public class Medicine {
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private String expiry;                 // format yyyy-MM-dd
    private boolean prescriptionRequired;

    public Medicine(int id, String name, String category, double price,
                    int quantity, String expiry, boolean prescriptionRequired) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expiry = expiry;
        this.prescriptionRequired = prescriptionRequired;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getExpiry() { return expiry; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
}
