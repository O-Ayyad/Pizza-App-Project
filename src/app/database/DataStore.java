package app.database;

import app.models.*;
import java.util.ArrayList;
import java.util.List;

public class DataStore {

    public static List<Account> accounts = new ArrayList<>();
    public static List<MenuItem> menuItems = new ArrayList<>();
    public static List<Topping> toppings = new ArrayList<>();
    public static List<Order> orders = new ArrayList<>();
    private static int nextOrderID = 1;
    private static int nextItemID = 1;
    private static int nextPaymentID = 1;
    private static int nextReceiptID = 1;

    public static boolean hasAnyAccounts() {
        //TODO: SQL here
        return !accounts.isEmpty();
    }

    public static Account findAccount(String username) {
        //TODO: SQL here
        for (Account acc : accounts) {
            if (acc.getUsername().equalsIgnoreCase(username)) return acc;
        }
        return null;
    }

    public static void addAccount(Account acc) {
        //TODO: SQL here
        accounts.add(acc);
    }
    
    public static void addMenuItem(MenuItem item) {
        //TODO: SQL here
        menuItems.add(item);
    }
    
    public static List<MenuItem> getMenuItems() {
        //TODO: SQL here
        return menuItems;
    }
    
    public static MenuItem findMenuItem(int id) {
        //TODO: SQL here
        for (MenuItem item : menuItems) {
            if (item.getMenuItemID() == id) return item;
        }
        return null;
    }
    
    public static void addTopping(Topping topping) {
        //TODO: SQL here
        toppings.add(topping);
    }
    
    public static List<Topping> getToppings() {
        //TODO: SQL here
        return toppings;
    }
    
    public static Topping findTopping(int id) {
        //TODO: SQL here
        for (Topping topping : toppings) {
            if (topping.getToppingID() == id) return topping;
        }
        return null;
    }
    
    public static int getNextOrderID() {
        //TODO: SQL here
        return nextOrderID++;
    }
    
    public static int getNextItemID() {
        //TODO: SQL here
        return nextItemID++;
    }
    
    public static int getNextPaymentID() {
        //TODO: SQL here
        return nextPaymentID++;
    }
    
    public static int getNextReceiptID() {
        //TODO: SQL here
        return nextReceiptID++;
    }
    
    public static void addOrder(Order order) {
        //TODO: SQL here
        orders.add(order);
    }
    
    public static List<Order> getOrders() {
        //TODO: SQL here
        return orders;
    }
    
    public static List<Order> getActiveOrders() {
        //TODO: SQL here
        List<Order> active = new ArrayList<>();
        for (Order order : orders) {
            String status = order.getStatus();
            // Active orders include: Pending, Preparing, Confirmed, Ready, Out for Delivery, Ready for Pickup, Delivery (Done)
            if (!"Completed".equals(status) && !"Cancelled".equals(status)) {
                active.add(order);
            }
        }
        return active;
    }
    
    public static void initializeDefaultData() {
        //TODO: SQL here  Initialize default with db
        addTopping(new Topping(1, "Pepperoni", 1.50));
        addTopping(new Topping(2, "Mushrooms", 1.00));
        addTopping(new Topping(3, "Onions", 1.00));
        addTopping(new Topping(4, "Sausage", 1.50));
        addTopping(new Topping(5, "Bacon", 2.00));
        addTopping(new Topping(6, "Extra Cheese", 1.50));
        addTopping(new Topping(7, "Peppers", 1.00));
        addTopping(new Topping(8, "Olives", 1.00));
        
        // Initialize default menu items
        addMenuItem(new MenuItem(1, "Margherita Pizza", "Pizza", 12.99));
        addMenuItem(new MenuItem(2, "Pepperoni Pizza", "Pizza", 14.99));
        addMenuItem(new MenuItem(3, "Supreme Pizza", "Pizza", 16.99));
        addMenuItem(new MenuItem(4, "Hawaiian Pizza", "Pizza", 15.99));
        addMenuItem(new MenuItem(5, "Veggie Pizza", "Pizza", 13.99));
        addMenuItem(new MenuItem(6, "Chocolate Cake", "Dessert", 6.99));
        addMenuItem(new MenuItem(7, "Cheesecake", "Dessert", 7.99));
        addMenuItem(new MenuItem(8, "Coca Cola", "Beverage", 2.99));
        addMenuItem(new MenuItem(9, "Pepsi", "Beverage", 2.99));
        addMenuItem(new MenuItem(10, "Sprite", "Beverage", 2.99));
    }
}