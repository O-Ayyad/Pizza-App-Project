package app.ui;

import app.models.CustomerAccount;
import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JFrame {
    private CustomerAccount customer;

    public CustomerDashboard(CustomerAccount acc){
        this.customer = acc;
        setTitle("Customer Dashboard - " + acc.getUsername());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Welcome, " + acc.getName() + "!", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton newOrderBtn = new JButton("Create New Order");
        JButton pastOrdersBtn = new JButton("View Past Orders");
        JButton updateProfileBtn = new JButton("Update Profile");
        JButton logoutBtn = new JButton("Logout");

        JButton[] buttons = {newOrderBtn, pastOrdersBtn, updateProfileBtn, logoutBtn};
        for (JButton b : buttons) {
            b.setBackground(new Color(200, 50, 50));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Arial", Font.BOLD, 16));
            b.setFocusPainted(false);
            buttonPanel.add(b);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);

        newOrderBtn.addActionListener(e -> {
            new MenuFrame(customer).setVisible(true);
        });

        pastOrdersBtn.addActionListener(e -> {
            new OrderHistoryFrame(customer).setVisible(true);
        });

        updateProfileBtn.addActionListener(e -> {
            new UpdateProfileFrame(customer).setVisible(true);
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
