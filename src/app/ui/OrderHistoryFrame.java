package app.ui;

import app.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderHistoryFrame extends JFrame {
    private CustomerAccount customer;

    public OrderHistoryFrame(CustomerAccount customer) {
        this.customer = customer;
        setTitle("Order History");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Your Past Orders", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        String[] columnNames = {"Order ID", "Date", "Type", "Total", "Status", "Rating"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Order> pastOrders = customer.viewPastOrders();
        for (Order order : pastOrders) {
            Object[] row = {
                order.getOrderID(),
                order.getTimestamp().toString(),
                order.getOrderType(),
                "$" + String.format("%.2f", order.getTotal()),
                order.getStatus(),
                order.getRating() > 0 ? order.getRating() + " stars" : "Not rated"
            };
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setBackground(new Color(200, 50, 50));
        viewDetailsBtn.setForeground(Color.WHITE);
        viewDetailsBtn.setFocusPainted(false);

        JButton viewReceiptBtn = new JButton("View Receipt");
        viewReceiptBtn.setBackground(new Color(34, 139, 34));
        viewReceiptBtn.setForeground(Color.WHITE);
        viewReceiptBtn.setFocusPainted(false);

        JButton pickupBtn = new JButton("Pick Up Order");
        pickupBtn.setBackground(new Color(100, 150, 200));
        pickupBtn.setForeground(Color.WHITE);
        pickupBtn.setFocusPainted(false);

        JButton repeatOrderBtn = new JButton("Repeat Order");
        repeatOrderBtn.setBackground(new Color(100, 150, 200));
        repeatOrderBtn.setForeground(Color.WHITE);
        repeatOrderBtn.setFocusPainted(false);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Color.GRAY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);

        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(viewReceiptBtn);
        buttonPanel.add(pickupBtn);
        buttonPanel.add(repeatOrderBtn);
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        viewDetailsBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an order to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderID = (Integer) model.getValueAt(selectedRow, 0);
            Order order = findOrder(orderID);
            if (order != null) {
                showOrderDetails(order);
            }
        });

        viewReceiptBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an order to view its receipt.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderID = (Integer) model.getValueAt(selectedRow, 0);
            Order order = findOrder(orderID);
            if (order != null) {
                if (!"Completed".equals(order.getStatus())) {
                    JOptionPane.showMessageDialog(this, "Receipt is only available for completed orders.", "Unavailable Receipt", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                showOrderReceipt(order);
            }
        });

        pickupBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an order to pick up.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderID = (Integer) model.getValueAt(selectedRow, 0);
            Order order = findOrder(orderID);
            if (order != null) {
                if (!"Ready for Pickup".equals(order.getStatus()) || !"Pickup".equals(order.getOrderType())) {
                    JOptionPane.showMessageDialog(this, "This order is not ready for pickup or is not a pickup order.", "Invalid Order", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Confirm that you have picked up order #" + orderID + "?", 
                    "Pick Up Order", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    order.setStatus("Completed");
                    model.setValueAt("Completed", selectedRow, 4);
                    JOptionPane.showMessageDialog(this, "Order marked as picked up! Thank you!", "Order Picked Up", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        repeatOrderBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an order to repeat.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderID = (Integer) model.getValueAt(selectedRow, 0);
            Order order = findOrder(orderID);
            if (order != null) {
                dispose();
                new MenuFrame(customer).setVisible(true);
                // In a real app, you'd pre-populate the cart with the previous order items
                JOptionPane.showMessageDialog(null, "Please add items to your cart. Previous order items are available in the menu.", "Repeat Order", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        closeBtn.addActionListener(e -> dispose());
    }

    // Helper function to display the order receipt for completed orders
    private void showOrderReceipt(Order order) {
        JDialog dialog = new JDialog(this, "Receipt for Order #" + order.getOrderID(), true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JTextArea receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        receiptArea.setBackground(Color.WHITE);

        // Simulate a more typical receipt format
        receiptArea.append("========= Pizza Shop Receipt =========\n");
        receiptArea.append("Order ID: " + order.getOrderID() + "\n");
        receiptArea.append("Date: " + order.getTimestamp().toString() + "\n");
        receiptArea.append("Customer: " + customer.getName() + "\n");
        receiptArea.append("--------------------------------------\n");
        for (OrderItem item : order.getOrderItems()) {
            receiptArea.append(item.formattingForReceipt() + "\n");
        }
        receiptArea.append("--------------------------------------\n");
        receiptArea.append(String.format("Subtotal: $%.2f\n", order.getSubtotal()));
        receiptArea.append(String.format("Tax: $%.2f\n", order.getTax()));
        receiptArea.append(String.format("Total: $%.2f\n", order.getTotal()));
        receiptArea.append("--------------------------------------\n");
        receiptArea.append("Order Status: " + order.getStatus() + "\n");
        if (order.getRating() > 0) {
            receiptArea.append("Rating: " + order.getRating() + " stars\n");
        }
        receiptArea.append("========= Thank you! =========\n");

        JScrollPane scrollPane = new JScrollPane(receiptArea);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private Order findOrder(int orderID) {
        for (Order order : customer.viewPastOrders()) {
            if (order.getOrderID() == orderID) {
                return order;
            }
        }
        return null;
    }

    private void showOrderDetails(Order order) {
        JDialog dialog = new JDialog(this, "Order Details #" + order.getOrderID(), true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setBackground(Color.WHITE);

        detailsArea.append("Order ID: " + order.getOrderID() + "\n");
        detailsArea.append("Date: " + order.getTimestamp().toString() + "\n");
        detailsArea.append("Type: " + order.getOrderType() + "\n");
        detailsArea.append("Status: " + order.getStatus() + "\n");
        if (order.getRating() > 0) {
            detailsArea.append("Rating: " + order.getRating() + " stars\n");
        }
        detailsArea.append("\nItems:\n");
        for (OrderItem item : order.getOrderItems()) {
            detailsArea.append(item.formattingForReceipt() + "\n");
        }
        detailsArea.append("\nSubtotal: $" + String.format("%.2f", order.getSubtotal()) + "\n");
        detailsArea.append("Tax: $" + String.format("%.2f", order.getTax()) + "\n");
        detailsArea.append("Total: $" + String.format("%.2f", order.getTotal()) + "\n");

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }
}


