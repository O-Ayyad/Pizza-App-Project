package app.ui;

import app.database.DataStore;
import app.models.*;
import app.models.MenuItem;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StaffCreateOrderFrame extends JFrame {
    private StaffAccount staff;
    private List<OrderItem> cart = new ArrayList<>();
    private JPanel cartPanel;
    private JLabel totalLabel;
    private CustomerAccount selectedCustomer;

    public StaffCreateOrderFrame(StaffAccount staff) {
        this.staff = staff;
        setTitle("Create Order - Staff");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(255, 245, 230));

        // Customer selection panel at top
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.setBackground(new Color(255, 245, 230));
        customerPanel.setBorder(new TitledBorder("Select Customer"));
        
        JLabel customerLabel = new JLabel("Customer:");
        JComboBox<String> customerCombo = new JComboBox<>();
        customerCombo.addItem("-- Select Customer --");
        for (Account acc : DataStore.accounts) {
            if (acc instanceof CustomerAccount) {
                CustomerAccount cust = (CustomerAccount) acc;
                String displayName = cust.getName() != null ? cust.getName() + " (" + cust.getUsername() + ")" : cust.getUsername();
                customerCombo.addItem(displayName);
            }
        }
        customerCombo.addActionListener(e -> {
            int index = customerCombo.getSelectedIndex();
            if (index > 0) {
                int customerIndex = 0;
                for (Account acc : DataStore.accounts) {
                    if (acc instanceof CustomerAccount) {
                        if (customerIndex == index - 1) {
                            selectedCustomer = (CustomerAccount) acc;
                            break;
                        }
                        customerIndex++;
                    }
                }
            } else {
                selectedCustomer = null;
            }
        });
        customerPanel.add(customerLabel);
        customerPanel.add(customerCombo);

        // Menu items panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new TitledBorder("Menu Items"));
        menuPanel.setBackground(new Color(255, 245, 230));

        JScrollPane menuScroll = new JScrollPane(menuPanel);
        menuScroll.setPreferredSize(new Dimension(500, 500));

        for (MenuItem item : DataStore.getMenuItems()) {
            JPanel itemPanel = createMenuItemPanel(item);
            menuPanel.add(itemPanel);
            menuPanel.add(Box.createVerticalStrut(10));
        }

        // Cart panel
        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBorder(new TitledBorder("Order Items"));
        cartPanel.setBackground(new Color(255, 245, 230));

        JScrollPane cartScroll = new JScrollPane(cartPanel);
        cartScroll.setPreferredSize(new Dimension(350, 400));

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(200, 50, 50));

        JButton createOrderBtn = new JButton("Create Order");
        createOrderBtn.setBackground(new Color(200, 50, 50));
        createOrderBtn.setForeground(Color.WHITE);
        createOrderBtn.setFont(new Font("Arial", Font.BOLD, 14));
        createOrderBtn.setFocusPainted(false);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setBackground(Color.GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        JPanel cartBottomPanel = new JPanel(new BorderLayout());
        cartBottomPanel.setBackground(new Color(255, 245, 230));
        cartBottomPanel.add(totalLabel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));
        buttonPanel.add(createOrderBtn);
        buttonPanel.add(backBtn);
        cartBottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(cartScroll, BorderLayout.CENTER);
        rightPanel.add(cartBottomPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(menuScroll, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(customerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        createOrderBtn.addActionListener(e -> {
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer.", "No Customer Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Order is empty! Please add items.", "Empty Order", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showOrderConfirmationDialog();
        });

        backBtn.addActionListener(e -> {
            dispose();
        });

        updateCartDisplay();
    }

    private JPanel createMenuItemPanel(MenuItem item) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel categoryLabel = new JLabel("Category: " + item.getCategory());
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", item.getBasePrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        priceLabel.setForeground(new Color(200, 50, 50));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(nameLabel);
        infoPanel.add(categoryLabel);
        infoPanel.add(priceLabel);

        JButton addBtn = new JButton("Add");
        addBtn.setBackground(new Color(200, 50, 50));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);

        if ("Pizza".equals(item.getCategory())) {
            addBtn.addActionListener(e -> showPizzaCustomizationDialog(item));
        } else {
            addBtn.addActionListener(e -> {
                OrderItem orderItem = new OrderItem(
                    DataStore.getNextItemID(), 0, item, "N/A", "N/A", new ArrayList<>()
                );
                cart.add(orderItem);
                updateCartDisplay();
            });
        }

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(addBtn, BorderLayout.EAST);

        return panel;
    }

    private void showPizzaCustomizationDialog(MenuItem item) {
        JDialog dialog = new JDialog(this, "Customize Pizza", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(nameLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Size:"), gbc);
        JComboBox<String> sizeCombo = new JComboBox<>(new String[]{"Small", "Medium", "Large"});
        gbc.gridx = 1;
        panel.add(sizeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Crust:"), gbc);
        JComboBox<String> crustCombo = new JComboBox<>(new String[]{"Thin", "Regular", "Thick", "Stuffed"});
        gbc.gridx = 1;
        panel.add(crustCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Additional Toppings:"), gbc);
        gbc.gridx = 1;
        JPanel toppingsPanel = new JPanel(new GridLayout(0, 1));
        JCheckBox[] toppingBoxes = new JCheckBox[DataStore.getToppings().size()];
        int i = 0;
        for (Topping topping : DataStore.getToppings()) {
            toppingBoxes[i] = new JCheckBox(topping.getName() + " (+$" + String.format("%.2f", topping.getBasePrice()) + ")");
            toppingsPanel.add(toppingBoxes[i]);
            i++;
        }
        JScrollPane toppingsScroll = new JScrollPane(toppingsPanel);
        toppingsScroll.setPreferredSize(new Dimension(200, 150));
        panel.add(toppingsScroll, gbc);

        JButton addBtn = new JButton("Add to Order");
        addBtn.setBackground(new Color(200, 50, 50));
        addBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);

        addBtn.addActionListener(e -> {
            String size = (String) sizeCombo.getSelectedItem();
            String crust = (String) crustCombo.getSelectedItem();
            List<Topping> selectedToppings = new ArrayList<>();
            for (int j = 0; j < toppingBoxes.length; j++) {
                if (toppingBoxes[j].isSelected()) {
                    selectedToppings.add(DataStore.getToppings().get(j));
                }
            }
            OrderItem orderItem = new OrderItem(
                DataStore.getNextItemID(), 0, item, size, crust, selectedToppings
            );
            cart.add(orderItem);
            updateCartDisplay();
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void updateCartDisplay() {
        cartPanel.removeAll();
        double total = 0.0;

        for (int i = 0; i < cart.size(); i++) {
            OrderItem item = cart.get(i);
            item.computePrice();
            total += item.getPrice();

            JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            itemPanel.setBackground(Color.WHITE);

            String itemText = item.getMenuItem().getName();
            if ("Pizza".equals(item.getMenuItem().getCategory())) {
                itemText += " - " + item.getSize() + ", " + item.getCrust();
                if (!item.getAdditionalToppings().isEmpty()) {
                    itemText += " (+" + item.getAdditionalToppings().size() + " toppings)";
                }
            }
            JLabel itemLabel = new JLabel("<html>" + itemText + "<br>$" + String.format("%.2f", item.getPrice()) + "</html>");
            itemLabel.setFont(new Font("Arial", Font.PLAIN, 11));

            JButton removeBtn = new JButton("Remove");
            removeBtn.setFont(new Font("Arial", Font.PLAIN, 10));
            final int index = i;
            removeBtn.addActionListener(e -> {
                cart.remove(index);
                updateCartDisplay();
            });

            itemPanel.add(itemLabel, BorderLayout.CENTER);
            itemPanel.add(removeBtn, BorderLayout.EAST);
            cartPanel.add(itemPanel);
            cartPanel.add(Box.createVerticalStrut(5));
        }

        double tax = total * 0.08;
        double finalTotal = total + tax;
        totalLabel.setText(String.format("Subtotal: $%.2f | Tax: $%.2f | Total: $%.2f", total, tax, finalTotal));

        cartPanel.revalidate();
        cartPanel.repaint();
    }

    private void showOrderConfirmationDialog() {
        JDialog dialog = new JDialog(this, "Confirm Order", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Order Summary", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(header, BorderLayout.NORTH);

        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        double subtotal = 0.0;
        for (OrderItem item : cart) {
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
        panel.add(summaryScroll, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        optionsPanel.add(new JLabel("Order Type:"), gbc);
        JComboBox<String> orderTypeCombo = new JComboBox<>(new String[]{"Pickup", "Delivery"});
        gbc.gridx = 1;
        optionsPanel.add(orderTypeCombo, gbc);

        JButton createBtn = new JButton("Create Order");
        createBtn.setBackground(new Color(200, 50, 50));
        createBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(optionsPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.add(panel);

        createBtn.addActionListener(e -> {
            String orderType = (String) orderTypeCombo.getSelectedItem();
            
            // Create order
            Order order = new Order(DataStore.getNextOrderID(), selectedCustomer, orderType);
            for (OrderItem item : cart) {
                order.addItem(item);
            }
            
            // Set status based on order type - staff creates orders that are ready
            if ("Pickup".equals(orderType)) {
                order.setStatus("Ready for Pickup");
            } else {
                order.setStatus("Delivery (Done)");
            }
            
            // Automatically process payment
            String paymentType = selectedCustomer.getPaymentPreference() != null ? 
                selectedCustomer.getPaymentPreference() : "Cash";
            Payment payment = new Payment(DataStore.getNextPaymentID(), order.getOrderID(), paymentType, order.getTotal());
            payment.processPayment(); // Automatic payment processing
            
            DataStore.addOrder(order);
            selectedCustomer.createOrder(order);

            JOptionPane.showMessageDialog(dialog, 
                "Order created successfully!\nOrder ID: " + order.getOrderID() + 
                "\nStatus: " + order.getStatus() + 
                "\nPayment processed automatically.",
                "Order Created", JOptionPane.INFORMATION_MESSAGE);
            
            dialog.dispose();
            dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
}


