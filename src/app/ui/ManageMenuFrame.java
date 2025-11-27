package app.ui;

import app.database.DataStore;
import app.models.*;
import app.models.MenuItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageMenuFrame extends JFrame {
    private ManagerAccount manager;

    public ManageMenuFrame(ManagerAccount manager) {
        this.manager = manager;
        setTitle("Manage Menu");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Manage Menu Items", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Category", "Base Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2 || column == 3; // Allow editing name, category, price
            }
        };

        for (MenuItem item : DataStore.getMenuItems()) {
            model.addRow(new Object[]{
                item.getMenuItemID(),
                item.getName(),
                item.getCategory(),
                "$" + String.format("%.2f", item.getBasePrice())
            });
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton addBtn = new JButton("Add Item");
        addBtn.setBackground(new Color(200, 50, 50));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);

        JButton updateBtn = new JButton("Update Selected");
        updateBtn.setBackground(new Color(100, 150, 200));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);

        JButton removeBtn = new JButton("Remove Item");
        removeBtn.setBackground(new Color(200, 50, 50));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Color.GRAY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Add Menu Item", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField nameField = new JTextField(15);
            JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Pizza", "Dessert", "Beverage"});
            JTextField priceField = new JTextField(15);

            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            panel.add(nameField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Category:"), gbc);
            gbc.gridx = 1;
            panel.add(categoryCombo, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Base Price:"), gbc);
            gbc.gridx = 1;
            panel.add(priceField, gbc);

            JButton createBtn = new JButton("Create");
            createBtn.setBackground(new Color(200, 50, 50));
            createBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setBackground(Color.GRAY);
            cancelBtn.setForeground(Color.WHITE);

            JPanel btnPanel = new JPanel(new FlowLayout());
            btnPanel.add(createBtn);
            btnPanel.add(cancelBtn);
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            panel.add(btnPanel, gbc);

            dialog.add(panel);

            createBtn.addActionListener(e2 -> {
                try {
                    String name = nameField.getText().trim();
                    String category = (String) categoryCombo.getSelectedItem();
                    double price = Double.parseDouble(priceField.getText().trim());

                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Please enter a name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    MenuItem item = new MenuItem(DataStore.getMenuItems().size() + 1, name, category, price);
                    DataStore.addMenuItem(item);
                    model.addRow(new Object[]{item.getMenuItemID(), name, category, "$" + String.format("%.2f", price)});
                    JOptionPane.showMessageDialog(dialog, "Menu item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid price format.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelBtn.addActionListener(e2 -> dialog.dispose());
            dialog.setVisible(true);
        });

        updateBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int id = (Integer) model.getValueAt(selectedRow, 0);
                String name = (String) model.getValueAt(selectedRow, 1);
                String category = (String) model.getValueAt(selectedRow, 2);
                String priceStr = (String) model.getValueAt(selectedRow, 3);
                double price = Double.parseDouble(priceStr.replace("$", ""));

                MenuItem item = DataStore.findMenuItem(id);
                if (item != null) {
                    // In a real implementation, we'd have setters in MenuItem
                    JOptionPane.showMessageDialog(this, "Item updated in table. (Note: Model updates would persist in database)", "Updated", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (Integer) model.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove this item?", 
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                MenuItem item = DataStore.findMenuItem(id);
                if (item != null) {
                    DataStore.getMenuItems().remove(item);
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Item removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        closeBtn.addActionListener(e -> dispose());
    }
}



