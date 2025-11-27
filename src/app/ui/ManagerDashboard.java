package app.ui;

import app.models.ManagerAccount;

import javax.swing.*;
import java.awt.*;

public class ManagerDashboard extends JFrame {

    public ManagerDashboard(ManagerAccount acc) {
        setTitle("Manager Dashboard");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Welcome, " + acc.getUsername() + "!", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton manageAccountsBtn = new JButton("Manage Accounts");
        JButton manageMenuBtn = new JButton("Manage Menu");
        JButton salesReportBtn = new JButton("Sales Report");
        JButton logoutBtn = new JButton("Logout");

        JButton[] buttons = {manageAccountsBtn, manageMenuBtn, salesReportBtn, logoutBtn};
        for (JButton b : buttons) {
            b.setBackground(new Color(200, 50, 50));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Arial", Font.BOLD, 16));
            b.setFocusPainted(false);
            buttonPanel.add(b);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);

        manageAccountsBtn.addActionListener(e -> {
            new ManageAccountsFrame(acc).setVisible(true);
        });

        manageMenuBtn.addActionListener(e -> {
            new ManageMenuFrame(acc).setVisible(true);
        });

        salesReportBtn.addActionListener(e -> {
            new SalesReportFrame(acc).setVisible(true);
        });
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
