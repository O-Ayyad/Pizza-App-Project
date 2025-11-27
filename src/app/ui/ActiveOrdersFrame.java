package app.ui;

import app.database.DataStore;
import app.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ActiveOrdersFrame extends JFrame {
    private StaffAccount staff;

    public ActiveOrdersFrame(StaffAccount staff) {
        this.staff = staff;
        setTitle("Active Orders");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Active Orders", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        String[] columnNames = {"Order ID", "Customer", "Date", "Type", "Total", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        refreshTable(model);

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setBackground(new Color(200, 50, 50));
        viewDetailsBtn.setForeground(Color.WHITE);
        viewDetailsBtn.setFocusPainted(false);

        JButton updateStatusBtn = new JButton("Update Status");
        updateStatusBtn.setBackground(new Color(100, 150, 200));
        updateStatusBtn.setForeground(Color.WHITE);
        updateStatusBtn.setFocusPainted(false);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(100, 150, 200));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Color.GRAY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);

        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(refreshBtn);
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

        updateStatusBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an order to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderID = (Integer) model.getValueAt(selectedRow, 0);
            Order order = findOrder(orderID);
            if (order != null) {
                updateOrderStatus(order, model, selectedRow);
            }
        });

        refreshBtn.addActionListener(e -> refreshTable(model));

        closeBtn.addActionListener(e -> dispose());
    }

    private void refreshTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Order order : DataStore.getActiveOrders()) {
            String customerName = order.getCustomer().getName() != null ? order.getCustomer().getName() : order.getCustomer().getUsername();
            model.addRow(new Object[]{
                order.getOrderID(),
                customerName,
                order.getTimestamp().toString(),
                order.getOrderType(),
                "$" + String.format("%.2f", order.getTotal()),
                order.getStatus()
            });
        }
    }

    private Order findOrder(int orderID) {
        for (Order order : DataStore.getOrders()) {
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
        detailsArea.append("Customer: " + (order.getCustomer().getName() != null ? order.getCustomer().getName() : order.getCustomer().getUsername()) + "\n");
        detailsArea.append("Date: " + order.getTimestamp().toString() + "\n");
        detailsArea.append("Type: " + order.getOrderType() + "\n");
        detailsArea.append("Status: " + order.getStatus() + "\n");
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

    private void updateOrderStatus(Order order, DefaultTableModel model, int row) {
        String currentStatus = order.getStatus();
        String[] statuses;
        
        // Staff can set orders to "Ready for Pickup" or "Delivery (Done)"
        // Delivery orders automatically complete when marked as "Delivery (Done)"
        if ("Pending".equals(currentStatus) || "Preparing".equals(currentStatus) || "Confirmed".equals(currentStatus)) {
            if ("Pickup".equals(order.getOrderType())) {
                statuses = new String[]{"Ready for Pickup", "Cancelled"};
            } else {
                statuses = new String[]{"Delivery (Done)", "Cancelled"};
            }
        } else if ("Ready for Pickup".equals(currentStatus)) {
            // Pickup orders stay ready until customer picks them up
            statuses = new String[]{"Completed", "Cancelled"};
        } else if ("Delivery (Done)".equals(currentStatus)) {
            // Delivery orders are automatically completed when delivered
            order.setStatus("Completed");
            model.setValueAt("Completed", row, 5);
            JOptionPane.showMessageDialog(this, "Delivery order marked as completed!", "Order Completed", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if ("Out for Delivery".equals(currentStatus)) {
            // When delivery is done, mark as completed
            statuses = new String[]{"Delivery (Done)", "Completed", "Cancelled"};
        } else if ("Ready".equals(currentStatus)) {
            if ("Pickup".equals(order.getOrderType())) {
                statuses = new String[]{"Ready for Pickup", "Cancelled"};
            } else {
                statuses = new String[]{"Out for Delivery", "Delivery (Done)", "Cancelled"};
            }
        } else if ("Completed".equals(currentStatus) || "Cancelled".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This order cannot be updated further.", "Status Update", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else {
            // For any other status, allow setting to ready states
            if ("Pickup".equals(order.getOrderType())) {
                statuses = new String[]{"Ready for Pickup", "Cancelled"};
            } else {
                statuses = new String[]{"Delivery (Done)", "Cancelled"};
            }
        }

        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Select new status:",
            "Update Order Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            statuses[0]
        );

        if (newStatus != null) {
            order.setStatus(newStatus);
            model.setValueAt(newStatus, row, 5);
            
            // If delivery order is marked as "Delivery (Done)", automatically complete it
            if ("Delivery (Done)".equals(newStatus)) {
                order.setStatus("Completed");
                model.setValueAt("Completed", row, 5);
                JOptionPane.showMessageDialog(this, "Delivery order marked as completed!", "Order Completed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Order status updated to: " + newStatus, "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}

