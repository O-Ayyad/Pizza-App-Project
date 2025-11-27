package app.models;

import app.database.DataStore;
import java.util.List;

public class StaffAccount extends Account {
    public StaffAccount(String username, String password) {
        super(username, password);
    }

    public List<Order> viewActiveOrders() {
        //TODO: SQL here
        // Returns all active orders that staff can manage
        return DataStore.getActiveOrders();
    }
    
    public void updateOrderStatus(Order order, String newStatus) {
        //TODO: SQL here
        if (order != null) {
            order.setStatus(newStatus);
        }
    }
    
    public void modifyOrder(Order order) {
        //TODO: SQL here
        if (order != null) {
            String status = order.getStatus();
            if ("Completed".equals(status) || "Cancelled".equals(status)) {
                return;
            }
            order.calculateTotals();
        }
    }
    
    public void addItemToOrder(Order order, OrderItem item) {
        //TODO: SQL here

        if (order != null && item != null) {
            String status = order.getStatus();
            if (!"Completed".equals(status) && !"Cancelled".equals(status)) {
                order.addItem(item);
            }
        }
    }
    
    public void removeItemFromOrder(Order order, OrderItem item) {
        //TODO: SQL here
        if (order != null && item != null) {
            String status = order.getStatus();
            if (!"Completed".equals(status) && !"Cancelled".equals(status)) {
                order.removeItem(item);
            }
        }
    }
    
    public void updateOrderType(Order order, String newOrderType) {
        //TODO: SQL here
        if (order != null && newOrderType != null) {
            String status = order.getStatus();
            if (!"Completed".equals(status) && !"Cancelled".equals(status)) {
                order.setOrderType(newOrderType);
            }
        }
    }
    
    public Payment handlePayment(Order order) {
        //TODO: SQL here
        
        if (order != null) {
            String paymentType = order.getCustomer().getPaymentPreference() != null ? 
                order.getCustomer().getPaymentPreference() : "Cash";
            Payment payment = new Payment(DataStore.getNextPaymentID(), order.getOrderID(), paymentType, order.getTotal());
            payment.processPayment();
            return payment;
        }
        return null;
    }
    
    public Receipt printReceipt(Order order) {
        //TODO: SQL here

        if (order != null) {
            Receipt receipt = new Receipt(DataStore.getNextReceiptID(), order.getOrderID());
            receipt.generateReceipt(order);
            return receipt;
        }
        return null;
    }
}
