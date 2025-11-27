package app.models;

import java.time.LocalDateTime;

public class Receipt {
    private int receiptID;
    private int orderID;
    private LocalDateTime generatedTimestamp;
    private String receiptContent;

    public Receipt(int receiptID, int orderID){
        this.receiptID = receiptID;
        this.orderID = orderID;
        this.generatedTimestamp = LocalDateTime.now();
    }

    public void generateReceipt(Order order){}
    public String formatReceipt(){ return ""; }
    public void printReceipt(){}
}
