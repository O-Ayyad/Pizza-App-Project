package app.ui;

import app.database.DataStore;
import app.models.*;

import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {
    private CustomerAccount customer;
    private Order order;

    public PaymentFrame(CustomerAccount customer, Order order) {
        this.customer = customer;
        this.order = order;
        setTitle("Payment");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Payment Information", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        JLabel totalLabel = new JLabel("Total Amount: $" + String.format("%.2f", order.getTotal()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(totalLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 245, 230));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        String paymentPref = customer.getPaymentPreference();
        JComboBox<String> paymentTypeCombo = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash"});
        if (paymentPref != null) {
            paymentTypeCombo.setSelectedItem(paymentPref);
        }

        JTextField cardNumberField = new JTextField(20);
        JTextField cardHolderField = new JTextField(20);
        JTextField expiryField = new JTextField(10);
        JTextField cvvField = new JTextField(5);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Payment Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(paymentTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Card Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cardNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Card Holder Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cardHolderField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Expiry (MM/YY):"), gbc);
        gbc.gridx = 1;
        formPanel.add(expiryField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("CVV:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cvvField, gbc);

        JCheckBox saveCardCheck = new JCheckBox("Save card for future orders");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(saveCardCheck, gbc);

        paymentTypeCombo.addActionListener(e -> {
            String type = (String) paymentTypeCombo.getSelectedItem();
            boolean isCard = !"Cash".equals(type);
            cardNumberField.setEnabled(isCard);
            cardHolderField.setEnabled(isCard);
            expiryField.setEnabled(isCard);
            cvvField.setEnabled(isCard);
            saveCardCheck.setEnabled(isCard);
        });
        paymentTypeCombo.setSelectedIndex(0);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton payBtn = new JButton("Pay Now");
        payBtn.setBackground(new Color(200, 50, 50));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Arial", Font.BOLD, 14));
        payBtn.setFocusPainted(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        buttonPanel.add(payBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        payBtn.addActionListener(e -> {
            String paymentType = (String) paymentTypeCombo.getSelectedItem();
            String cardNumber = cardNumberField.getText().trim();

            if (!"Cash".equals(paymentType)) {
                if (cardNumber.isEmpty() || cardHolderField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all payment fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Payment payment = new Payment(DataStore.getNextPaymentID(), order.getOrderID(), paymentType, order.getTotal());
            if (!"Cash".equals(paymentType)) {
                // DEMO APP: Card validation accepts any non-empty input
                if (!payment.validateCard(cardNumber)) {
                    JOptionPane.showMessageDialog(this, "Please enter a card number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (payment.processPayment()) {
                order.setStatus("Confirmed");
                if (saveCardCheck.isSelected() && !"Cash".equals(paymentType)) {
                    customer.updatePaymentPreference(paymentType);
                }

                Receipt receipt = new Receipt(DataStore.getNextReceiptID(), order.getOrderID());
                receipt.generateReceipt(order);

                JOptionPane.showMessageDialog(this, 
                    "Payment successful!\nOrder ID: " + order.getOrderID() + "\nYour order is being prepared.",
                    "Payment Confirmed", JOptionPane.INFORMATION_MESSAGE);

                dispose();
                new OrderTrackingFrame(customer, order).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Payment failed. Please try again.", "Payment Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this payment?", "Cancel Payment", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                order.setStatus("Cancelled");
                dispose();
            }
        });
    }
}

