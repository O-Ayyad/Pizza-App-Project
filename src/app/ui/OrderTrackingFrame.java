package app.ui;

import app.models.*;

import javax.swing.*;
import java.awt.*;

public class OrderTrackingFrame extends JFrame {
    private CustomerAccount customer;
    private Order order;
    private JLabel statusLabel;
    private Timer statusTimer;

    public OrderTrackingFrame(CustomerAccount customer, Order order) {
        this.customer = customer;
        this.order = order;
        setTitle("Order Tracking - Order #" + order.getOrderID());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Order #" + order.getOrderID(), SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(new Color(255, 245, 230));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        statusLabel = new JLabel("Status: " + order.getStatus());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        infoPanel.add(statusLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Order Type:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(order.getOrderType()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Total:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel("$" + String.format("%.2f", order.getTotal())), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Order Time:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(order.getTimestamp().toString()), gbc);

        // Order items
        JTextArea itemsArea = new JTextArea();
        itemsArea.setEditable(false);
        itemsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        itemsArea.setBackground(Color.WHITE);
        for (OrderItem item : order.getOrderItems()) {
            itemsArea.append(item.formattingForReceipt() + "\n");
        }
        JScrollPane itemsScroll = new JScrollPane(itemsArea);
        itemsScroll.setPreferredSize(new Dimension(400, 150));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(new JLabel("Items:"), gbc);
        gbc.gridy = 5;
        infoPanel.add(itemsScroll, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Rating panel (only show if order is completed)
        final JPanel[] ratingPanel = {new JPanel(new FlowLayout())};
        ratingPanel[0].setBackground(new Color(255, 245, 230));
        if ("Completed".equals(order.getStatus()) && order.getRating() == 0) {
            ratingPanel[0].add(new JLabel("Rate your experience:"));
            JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
            JButton submitRatingBtn = new JButton("Submit Rating");
            submitRatingBtn.setBackground(new Color(200, 50, 50));
            submitRatingBtn.setForeground(Color.WHITE);
            ratingPanel[0].add(ratingCombo);
            ratingPanel[0].add(submitRatingBtn);

            submitRatingBtn.addActionListener(e -> {
                int rating = (Integer) ratingCombo.getSelectedItem();
                order.setRating(rating);
                JOptionPane.showMessageDialog(this, "Thank you for your feedback!", "Rating Submitted", JOptionPane.INFORMATION_MESSAGE);
                ratingPanel[0].removeAll();
                ratingPanel[0].add(new JLabel("Rating: " + rating + " stars"));
                ratingPanel[0].revalidate();
                ratingPanel[0].repaint();
            });
        } else if (order.getRating() > 0) {
            ratingPanel[0].add(new JLabel("Your Rating: " + order.getRating() + " stars"));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));

        // Add "Pick Up Order" button if order is ready for pickup
        JButton pickupBtn;
        if ("Ready for Pickup".equals(order.getStatus()) && "Pickup".equals(order.getOrderType())) {
            pickupBtn = new JButton("I Picked Up My Order");
            pickupBtn.setBackground(new Color(200, 50, 50));
            pickupBtn.setForeground(Color.WHITE);
            pickupBtn.setFont(new Font("Arial", Font.BOLD, 14));
            pickupBtn.setFocusPainted(false);
            buttonPanel.add(pickupBtn);
        } else {
            pickupBtn = null;
        }

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setBackground(Color.GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        buttonPanel.add(backBtn);

        if (ratingPanel[0].getComponentCount() > 0) {
            mainPanel.add(ratingPanel[0], BorderLayout.SOUTH);
        } else {
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        add(mainPanel);

        // Simulate order status updates
        statusTimer = new Timer(5000, e -> updateOrderStatus());
        statusTimer.start();

        if (pickupBtn != null) {
            pickupBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Confirm that you have picked up your order?", 
                    "Pick Up Order", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    order.setStatus("Completed");
                    statusLabel.setText("Status: " + order.getStatus());
                    statusTimer.stop();
                    
                    // Show rating panel after pickup
                    ratingPanel[0] = new JPanel(new FlowLayout());
                    ratingPanel[0].setBackground(new Color(255, 245, 230));
                    ratingPanel[0].add(new JLabel("Rate your experience:"));
                    JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
                    JButton submitRatingBtn = new JButton("Submit Rating");
                    submitRatingBtn.setBackground(new Color(200, 50, 50));
                    submitRatingBtn.setForeground(Color.WHITE);
                    ratingPanel[0].add(ratingCombo);
                    ratingPanel[0].add(submitRatingBtn);

                    submitRatingBtn.addActionListener(e2 -> {
                        int rating = (Integer) ratingCombo.getSelectedItem();
                        order.setRating(rating);
                        JOptionPane.showMessageDialog(this, "Thank you for your feedback!", "Rating Submitted", JOptionPane.INFORMATION_MESSAGE);
                        ratingPanel[0].removeAll();
                        ratingPanel[0].add(new JLabel("Rating: " + rating + " stars"));
                        ratingPanel[0].revalidate();
                        ratingPanel[0].repaint();
                    });

                    getContentPane().remove(getContentPane().getComponentCount() - 1);
                    getContentPane().add(ratingPanel[0], BorderLayout.SOUTH);
                    buttonPanel.remove(pickupBtn);
                    revalidate();
                    repaint();
                }
            });
        }

        backBtn.addActionListener(e -> {
            statusTimer.stop();
            dispose();
        });
    }

    private void updateOrderStatus() {
        String currentStatus = order.getStatus();
        if ("Confirmed".equals(currentStatus)) {
            order.setStatus("Preparing");
        } else if ("Preparing".equals(currentStatus)) {
            order.setStatus("Ready");
        } else if ("Ready".equals(currentStatus)) {
            if ("Delivery".equals(order.getOrderType())) {
                order.setStatus("Out for Delivery");
            } else {
                order.setStatus("Ready for Pickup");
                // Stop timer - customer must pick up manually
                statusTimer.stop();
            }
        } else if ("Out for Delivery".equals(currentStatus)) {
            order.setStatus("Completed");
            statusTimer.stop();
        } else if ("Delivery (Done)".equals(currentStatus)) {
            order.setStatus("Completed");
            statusTimer.stop();
        } else if ("Ready for Pickup".equals(currentStatus)) {
            statusTimer.stop();
        } else {
            statusTimer.stop();
        }
        statusLabel.setText("Status: " + order.getStatus());
        
        if ("Completed".equals(order.getStatus()) && order.getRating() == 0) {
            // Show rating panel
            JPanel ratingPanel = new JPanel(new FlowLayout());
            ratingPanel.setBackground(new Color(255, 245, 230));
            ratingPanel.add(new JLabel("Rate your experience:"));
            JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
            JButton submitRatingBtn = new JButton("Submit Rating");
            submitRatingBtn.setBackground(new Color(200, 50, 50));
            submitRatingBtn.setForeground(Color.WHITE);
            ratingPanel.add(ratingCombo);
            ratingPanel.add(submitRatingBtn);

            submitRatingBtn.addActionListener(e -> {
                int rating = (Integer) ratingCombo.getSelectedItem();
                order.setRating(rating);
                JOptionPane.showMessageDialog(this, "Thank you for your feedback!", "Rating Submitted", JOptionPane.INFORMATION_MESSAGE);
                ratingPanel.removeAll();
                ratingPanel.add(new JLabel("Rating: " + rating + " stars"));
                ratingPanel.revalidate();
                ratingPanel.repaint();
            });

            getContentPane().remove(getContentPane().getComponentCount() - 1);
            getContentPane().add(ratingPanel, BorderLayout.SOUTH);
            revalidate();
            repaint();
        }
    }
}

