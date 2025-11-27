package app.ui;

import app.database.DataStore;
import app.models.*;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("PizzaApp Login");
        setSize(400, 300);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230)); // Light pizza-like background

        // Header
        JLabel header = new JLabel("Welcome to PizzaApp ", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50)); // Pizza-red
        mainPanel.add(header, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 245, 230));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setToolTipText("Enter your username");

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setToolTipText("Enter your password");

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(200, 50, 50));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(100, 150, 200));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 12));

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(loginBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(registerBtn, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Button action
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            Account acc = DataStore.findAccount(username);
            if(acc == null || !acc.verifyPassword(password)){
                JOptionPane.showMessageDialog(this,"Invalid login. Please check your username and password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dispose();
            if(acc instanceof ManagerAccount) new ManagerDashboard((ManagerAccount) acc).setVisible(true);
            if(acc instanceof StaffAccount) new StaffDashboard((StaffAccount) acc).setVisible(true);
            if(acc instanceof CustomerAccount) new CustomerDashboard((CustomerAccount) acc).setVisible(true);
        });

        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
