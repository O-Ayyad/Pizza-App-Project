package app.ui;

import app.database.DataStore;
import app.models.*;
import app.models.MenuItem;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuFrame extends JFrame {
    private CustomerAccount customer;
    private List<OrderItem> cart = new ArrayList<>();
    private JPanel cartPanel;
    private JLabel totalLabel;

    public MenuFrame(CustomerAccount customer) {
        this.customer = customer;
        setTitle("Menu - " + customer.getUsername());
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(255, 245, 230));

        // Menu items panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new TitledBorder("Menu Items"));
        menuPanel.setBackground(new Color(255, 245, 230));

        JScrollPane menuScroll = new JScrollPane(menuPanel);
        menuScroll.setPreferredSize(new Dimension(500, 600));

        for (MenuItem item : DataStore.getMenuItems()) {
            JPanel itemPanel = createMenuItemPanel(item);
            menuPanel.add(itemPanel);
            menuPanel.add(Box.createVerticalStrut(10));
        }

        //Cart panel
        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBorder(new TitledBorder("Shopping Cart"));
        cartPanel.setBackground(new Color(255, 245, 230));

        JScrollPane cartScroll = new JScrollPane(cartPanel);
        cartScroll.setPreferredSize(new Dimension(350, 500));

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(200, 50, 50));

        JButton checkoutBtn = new JButton("Proceed to Checkout");
        checkoutBtn.setBackground(new Color(200, 50, 50));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutBtn.setFocusPainted(false);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setBackground(Color.GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        JPanel cartBottomPanel = new JPanel(new BorderLayout());
        cartBottomPanel.setBackground(new Color(255, 245, 230));
        cartBottomPanel.add(totalLabel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(backBtn);
        cartBottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(cartScroll, BorderLayout.CENTER);
        rightPanel.add(cartBottomPanel, BorderLayout.SOUTH);

        mainPanel.add(menuScroll, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);

        checkoutBtn.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new OrderPlacementFrame(customer, new ArrayList<>(cart)).setVisible(true);
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

        JButton addBtn = new JButton("Add to Cart");
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

        JButton addBtn = new JButton("Add to Cart");
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
}


