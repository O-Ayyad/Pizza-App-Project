package app.ui;

import app.database.DataStore;
import app.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HandlePaymentsFrame extends JFrame {
    private StaffAccount staff;

    public HandlePaymentsFrame(StaffAccount staff) {
        this.staff = staff;
        setTitle("Handle Payments");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Handle Payments", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        String[] columnNames = {"Order ID", "Customer", "Amount", "Payment Type", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Show orders that need payment processing
        for (Order order : DataStore.getOrders()) {
            if (!"Cancelled".equals(order.getStatus()) && !"Completed".equals(order.getStatus())) {
                String customerName = order.getCustomer().getName() != null ? order.getCustomer().getName() : order.getCustomer().getUsername();
                String paymentType = order.getCustomer().getPaymentPreference() != null ? order.getCustomer().getPaymentPreference() : "Not specified";
                model.addRow(new Object[]{
                    order.getOrderID(),
                    customerName,
                    "$" + String.format("%.2f", order.getTotal()),
                    paymentType,
                    order.getStatus()
                });
            }
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton processPaymentBtn = new JButton("Process Payment");
        processPaymentBtn.setBackground(new Color(200, 50, 50));
        processPaymentBtn.setForeground(Color.WHITE);
        processPaymentBtn.setFocusPainted(false);

        JButton printReceiptBtn = new JButton("Print Receipt");
        printReceiptBtn.setBackground(new Color(100, 150, 200));
        printReceiptBtn.setForeground(Color.WHITE);
        printReceiptBtn.setFocusPainted(false);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Color.GRAY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);

        buttonPanel.add(processPaymentBtn);
        buttonPanel.add(printReceiptBtn);
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        processPaymentBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an order to process payment.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderID = (Integer) model.getValueAt(selectedRow, 0);
            Order order = findOrder(orderID);
            if (order != null) {
                processPayment(order);
            }
        });

        printReceiptBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an order to print receipt.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderID = (Integer) model.getValueAt(selectedRow, 0);
            Order order = findOrder(orderID);
            if (order != null) {
                printReceipt(order);
            }
        });

        closeBtn.addActionListener(e -> dispose());
    }

    private Order findOrder(int orderID) {
        for (Order order : DataStore.getOrders()) {
            if (order.getOrderID() == orderID) {
                return order;
            }
        }
        return null;
    }

    private void processPayment(Order order) {
        String paymentType = order.getCustomer().getPaymentPreference();
        if (paymentType == null) {
            paymentType = "Cash";
        }

        Payment payment = new Payment(DataStore.getNextPaymentID(), order.getOrderID(), paymentType, order.getTotal());
        
        if ("Cash".equals(paymentType)) {
            payment.setStatus("Completed");
            payment.processPayment();
            order.setStatus("Confirmed");
            JOptionPane.showMessageDialog(this, 
                "Cash payment processed successfully!\nOrder ID: " + order.getOrderID(),
                "Payment Processed", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // For card payments, show a dialog to enter card details
            JDialog dialog = new JDialog(this, "Process Card Payment", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField cardNumberField = new JTextField(20);
            JTextField cardHolderField = new JTextField(20);

            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Card Number:"), gbc);
            gbc.gridx = 1;
            panel.add(cardNumberField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Card Holder:"), gbc);
            gbc.gridx = 1;
            panel.add(cardHolderField, gbc);

            JButton processBtn = new JButton("Process");
            processBtn.setBackground(new Color(200, 50, 50));
            processBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setBackground(Color.GRAY);
            cancelBtn.setForeground(Color.WHITE);

            JPanel btnPanel = new JPanel(new FlowLayout());
            btnPanel.add(processBtn);
            btnPanel.add(cancelBtn);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            panel.add(btnPanel, gbc);

            dialog.add(panel);

            processBtn.addActionListener(e -> {
                String cardNumber = cardNumberField.getText().trim();
                // DEMO APP: Card validation accepts any non-empty input
                if (cardNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a card number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (payment.validateCard(cardNumber) && payment.processPayment()) {
                    order.setStatus("Confirmed");
                    JOptionPane.showMessageDialog(dialog, 
                        "Card payment processed successfully!\nOrder ID: " + order.getOrderID(),
                        "Payment Processed", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Payment processing failed. Please try again.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelBtn.addActionListener(e -> dialog.dispose());
            dialog.setVisible(true);
        }
    }

    private void printReceipt(Order order) {
        Receipt receipt = new Receipt(DataStore.getNextReceiptID(), order.getOrderID());
        receipt.generateReceipt(order);

        JDialog dialog = new JDialog(this, "Receipt - Order #" + order.getOrderID(), true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTextArea receiptArea = new JTextArea(receipt.formatReceipt());
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(receiptArea);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }
}

