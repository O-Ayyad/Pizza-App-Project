package app.ui;

import app.database.DataStore;
import app.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SalesReportFrame extends JFrame {
    private ManagerAccount manager;

    public SalesReportFrame(ManagerAccount manager) {
        this.manager = manager;
        setTitle("Sales Report");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Sales Report", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        String[] columnNames = {"Order ID", "Customer", "Date", "Type", "Subtotal", "Tax", "Total", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Order> orders = DataStore.getOrders();
        double totalRevenue = 0.0;
        int totalOrders = orders.size();
        int completedOrders = 0;

        for (Order order : orders) {
            String customerName = order.getCustomer().getName() != null ? order.getCustomer().getName() : order.getCustomer().getUsername();
            model.addRow(new Object[]{
                order.getOrderID(),
                customerName,
                order.getTimestamp().toString(),
                order.getOrderType(),
                "$" + String.format("%.2f", order.getSubtotal()),
                "$" + String.format("%.2f", order.getTax()),
                "$" + String.format("%.2f", order.getTotal()),
                order.getStatus()
            });
            totalRevenue += order.getTotal();
            if ("Completed".equals(order.getStatus())) {
                completedOrders++;
            }
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        summaryPanel.setBackground(new Color(255, 245, 230));

        summaryPanel.add(new JLabel("Total Orders:"));
        summaryPanel.add(new JLabel(String.valueOf(totalOrders)));
        summaryPanel.add(new JLabel("Completed Orders:"));
        summaryPanel.add(new JLabel(String.valueOf(completedOrders)));
        summaryPanel.add(new JLabel("Total Revenue:"));
        JLabel revenueLabel = new JLabel("$" + String.format("%.2f", totalRevenue));
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        revenueLabel.setForeground(new Color(200, 50, 50));
        summaryPanel.add(revenueLabel);
        summaryPanel.add(new JLabel("Average Order Value:"));
        double avgOrder = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;
        summaryPanel.add(new JLabel("$" + String.format("%.2f", avgOrder)));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(255, 245, 230));
        bottomPanel.add(summaryPanel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Color.GRAY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));
        buttonPanel.add(closeBtn);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
}



