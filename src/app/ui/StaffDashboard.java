package app.ui;

import app.models.StaffAccount;

import javax.swing.*;
import java.awt.*;

public class StaffDashboard extends JFrame {

    public StaffDashboard(StaffAccount acc) {
        setTitle("Staff Dashboard ");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        // Header
        JLabel header = new JLabel("Welcome, " + acc.getUsername() + "!", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton createOrderBtn = new JButton("Create Order");
        JButton viewOrdersBtn = new JButton("View Active Orders");
        JButton logoutBtn = new JButton("Logout");

        JButton[] buttons = {createOrderBtn, viewOrdersBtn, logoutBtn};
        for (JButton b : buttons) {
            b.setBackground(new Color(200, 50, 50));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Arial", Font.BOLD, 16));
            b.setFocusPainted(false);
            buttonPanel.add(b);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);

        createOrderBtn.addActionListener(e -> {
            new StaffCreateOrderFrame(acc).setVisible(true);
        });

        viewOrdersBtn.addActionListener(e -> {
            new ActiveOrdersFrame(acc).setVisible(true);
        });
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
