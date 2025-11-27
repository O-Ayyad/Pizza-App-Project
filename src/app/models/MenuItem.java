package app.models;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
    private int menuItemID;
    private String name;
    private String category;
    private double basePrice;
    private List<Topping> toppings = new ArrayList<>();

    public MenuItem(int menuItemID, String name, String category, double basePrice){
        this.menuItemID = menuItemID;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
    }

    public double getBasePrice() { return basePrice; }
    public List<Topping> listToppings() { return toppings; }
    public int getMenuItemID() { return menuItemID; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public void addTopping(Topping topping) { 
        //TODO: SQL here
        toppings.add(topping); 
    }
}
