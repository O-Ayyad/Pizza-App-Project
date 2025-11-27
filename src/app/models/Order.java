package app.models;

import app.models.CustomerAccount;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderID;
    private CustomerAccount customer;
    private List<OrderItem> orderItems = new ArrayList<>();
    private String orderType;
    private LocalDateTime timestamp;
    private double subtotal;
    private double tax;
    private double total;

    public Order(int orderID, CustomerAccount customer, String type){
        this.orderID = orderID;
        this.customer = customer;
        this.orderType = type;
        this.timestamp = LocalDateTime.now();
    }

    public void addItem(OrderItem item) { 
        //TODO: SQL here
        item.setOrderID(this.orderID);
        item.computePrice();
        orderItems.add(item); 
        calculateTotals();
    }
    
    public void removeItem(OrderItem item) { 
        //TODO: SQL here
        orderItems.remove(item); 
        calculateTotals();
    }
    
    public void calculateTotals() {
        subtotal = 0.0;
        for (OrderItem item : orderItems) {
            subtotal += item.getPrice();
        }
        tax = subtotal * 0.08; // 8% tax
        total = subtotal + tax;
    }
    
    public void setOrderType(String type) { 
        //TODO: SQL here
        this.orderType = type; 
    }
    
    // Getters
    public int getOrderID() { return orderID; }
    public CustomerAccount getCustomer() { return customer; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public String getOrderType() { return orderType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    
    private String status = "Pending";
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        //TODO: SQL here
        this.status = status; 
    }
    
    private int rating = 0;
    public int getRating() { return rating; }
    public void setRating(int rating) { 
        //TODO: SQL here
        this.rating = rating; 
    }
}
