package app.models;

public class Topping {
    private int toppingID;
    private String name;
    private double basePrice;

    public Topping(int toppingID, double basePrice) {
        this.toppingID = toppingID;
        this.basePrice = basePrice;
        this.name = "Topping #" + toppingID;
    }
    
    public Topping(int toppingID, String name, double basePrice) {
        this.toppingID = toppingID;
        this.name = name;
        this.basePrice = basePrice;
    }

    public double getBasePrice() { return basePrice; }
    public int getToppingID() { return toppingID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
