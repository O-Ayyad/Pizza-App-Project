package app.models;

import app.models.MenuItem;
import app.models.Topping;
import java.util.List;

public class OrderItem {
    private int itemID;
    private int orderID;
    private MenuItem menuItem;
    private String size;
    private String crust;
    private List<Topping> additionalToppings;
    private double price;

    public OrderItem(int itemID, int orderID, MenuItem menuItem, String size, String crust, List<Topping> toppings){
        this.itemID = itemID;
        this.orderID = orderID;
        this.menuItem = menuItem;
        this.size = size;
        this.crust = crust;
        this.additionalToppings = toppings;
    }

    public void computePrice() {
        double basePrice = menuItem.getBasePrice();
        
        // Size multiplier
        double sizeMultiplier = 1.0;
        if ("Large".equalsIgnoreCase(size)) {
            sizeMultiplier = 1.5;
        } else if ("Medium".equalsIgnoreCase(size)) {
            sizeMultiplier = 1.25;
        } else if ("Small".equalsIgnoreCase(size)) {
            sizeMultiplier = 1.0;
        }
        
        // Crust premium (if any)
        double crustPremium = 0.0;
        if ("Thick".equalsIgnoreCase(crust) || "Stuffed".equalsIgnoreCase(crust)) {
            crustPremium = 2.0;
        }
        
        // Additional toppings
        double toppingsPrice = 0.0;
        if (additionalToppings != null) {
            for (Topping topping : additionalToppings) {
                toppingsPrice += topping.getBasePrice();
            }
        }
        
        this.price = (basePrice * sizeMultiplier) + crustPremium + toppingsPrice;
    }
    
    public String formattingForReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append(menuItem.getName()).append(" - ");
        sb.append(size).append(", ").append(crust);
        if (additionalToppings != null && !additionalToppings.isEmpty()) {
            sb.append("\n  Toppings: ");
            for (Topping t : additionalToppings) {
                sb.append("Topping #").append(t.getToppingID()).append(" ");
            }
        }
        sb.append("\n  Price: $").append(String.format("%.2f", price));
        return sb.toString();
    }
    
    public double getPrice() { return price; }
    public MenuItem getMenuItem() { return menuItem; }
    public String getSize() { return size; }
    public String getCrust() { return crust; }
    public List<Topping> getAdditionalToppings() { return additionalToppings; }
    public void setOrderID(int orderID) { this.orderID = orderID; }
    public int getOrderID() { return orderID; }
}
