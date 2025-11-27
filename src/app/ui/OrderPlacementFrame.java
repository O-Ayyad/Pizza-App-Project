package app.ui;

import app.database.DataStore;
import app.models.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderPlacementFrame extends JFrame {
    private CustomerAccount customer;
    private List<OrderItem> cartItems;
    private Order currentOrder;

    public OrderPlacementFrame(CustomerAccount customer, List<OrderItem> cartItems) {
        this.customer = customer;
        this.cartItems = cartItems;
        setTitle("Place Order");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Review Your Order", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        // Order summary
        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setBackground(Color.WHITE);
        
        double subtotal = 0.0;
        for (OrderItem item : cartItems) {
            item.computePrice();
            subtotal += item.getPrice();
            summaryArea.append(item.formattingForReceipt() + "\n\n");
        }
        double tax = subtotal * 0.08;
        double total = subtotal + tax;
        summaryArea.append("Subtotal: $" + String.format("%.2f", subtotal) + "\n");
        summaryArea.append("Tax: $" + String.format("%.2f", tax) + "\n");
        summaryArea.append("Total: $" + String.format("%.2f", total) + "\n");

        JScrollPane summaryScroll = new JScrollPane(summaryArea);
        mainPanel.add(summaryScroll, BorderLayout.CENTER);

        // Delivery options
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBackground(new Color(255, 245, 230));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        optionsPanel.add(new JLabel("Order Type:"), gbc);
        JComboBox<String> orderTypeCombo = new JComboBox<>(new String[]{"Pickup", "Delivery"});
        gbc.gridx = 1;
        optionsPanel.add(orderTypeCombo, gbc);

        JTextField deliveryAddressField = new JTextField(25);
        deliveryAddressField.setText(customer.getAddress());
        gbc.gridx = 0; gbc.gridy = 1;
        optionsPanel.add(new JLabel("Delivery Address:"), gbc);
        gbc.gridx = 1;
        optionsPanel.add(deliveryAddressField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton confirmBtn = new JButton("Confirm Order");
        confirmBtn.setBackground(new Color(200, 50, 50));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 14));
        confirmBtn.setFocusPainted(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setBackground(Color.GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(backBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(255, 245, 230));
        bottomPanel.add(optionsPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        orderTypeCombo.addActionListener(e -> {
            String type = (String) orderTypeCombo.getSelectedItem();
            deliveryAddressField.setEnabled("Delivery".equals(type));
        });
        deliveryAddressField.setEnabled(false);

        confirmBtn.addActionListener(e -> {
            String orderType = (String) orderTypeCombo.getSelectedItem();
            String address = deliveryAddressField.getText().trim();

            if ("Delivery".equals(orderType) && address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide a delivery address.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create order
            currentOrder = new Order(DataStore.getNextOrderID(), customer, orderType);
            for (OrderItem item : cartItems) {
                currentOrder.addItem(item);
            }
            currentOrder.setStatus("Pending");
            DataStore.addOrder(currentOrder);
            customer.createOrder(currentOrder);

            new PaymentFrame(customer, currentOrder).setVisible(true);
        });

        cancelBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this order?", "Cancel Order", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
        });
    }
}


