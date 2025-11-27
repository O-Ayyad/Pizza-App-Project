package app;

import app.database.DataStore;
import app.ui.CreateManagerFrame;
import app.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Initialize default menu items and toppings
        DataStore.initializeDefaultData();

        if (!DataStore.hasAnyAccounts()) {
            new CreateManagerFrame().setVisible(true);
        } else {
            new LoginFrame().setVisible(true);
        }
    }
}
