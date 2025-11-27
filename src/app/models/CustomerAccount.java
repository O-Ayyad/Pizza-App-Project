package app.models;

import java.util.ArrayList;
import java.util.List;

public class CustomerAccount extends Account {

    private String phoneNumber;
    private String name;
    private String address;
    private List<Order> pastOrders = new ArrayList<>();
    private String paymentPreference;

    public CustomerAccount(String username, String password, String phone, String name, String address, String paymentPref) {
        super(username, password);
        this.phoneNumber = phone;
        this.name = name;
        this.address = address;
        this.paymentPreference = paymentPref;
    }

    public List<Order> viewPastOrders() { 
        //TODO: SQL here
        return pastOrders; 
    }
    
    public void createOrder(Order order) { 
        //TODO: SQL here
        pastOrders.add(order);
    }
    
    public void updatePaymentPreference(String pref) { 
        //TODO: SQL here
        this.paymentPreference = pref; 
    }
    
    public String getPhoneNumber() { return phoneNumber; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPaymentPreference() { return paymentPreference; }
    public void setPhoneNumber(String phone) { this.phoneNumber = phone; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
}
