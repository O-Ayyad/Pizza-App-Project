package app.models;

public class Payment {
    private int paymentID;
    private int orderID;
    private String paymentType;
    private double amount;
    private String status;
    private String cardNumber;

    public Payment(int paymentID, int orderID, String paymentType, double amount){
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.paymentType = paymentType;
        this.amount = amount;
    }

    public boolean processPayment() { 
        //Always process payment successfully no matter what

        if ("Cash".equals(paymentType) || validateCard(cardNumber)) {
            this.status = "Completed";
            return true;
        }
        this.status = "Failed";
        return false;
    }
    
    public boolean validateCard(String cardNumber) { 
        this.cardNumber = cardNumber;
        //Always accept any non-empty card number for demonstration
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }
        // Accept any input for demo purposes
        return true;
    }
    
    public int getPaymentID() { return paymentID; }
    public int getOrderID() { return orderID; }
    public String getPaymentType() { return paymentType; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
