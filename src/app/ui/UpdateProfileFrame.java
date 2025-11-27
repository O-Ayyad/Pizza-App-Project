package app.ui;

import app.models.CustomerAccount;

import javax.swing.*;
import java.awt.*;

public class UpdateProfileFrame extends JFrame {
    private CustomerAccount customer;

    public UpdateProfileFrame(CustomerAccount customer) {
        this.customer = customer;
        setTitle("Update Profile");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Update Your Profile", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 245, 230));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(20);
        nameField.setText(customer.getName());
        JTextField phoneField = new JTextField(20);
        phoneField.setText(customer.getPhoneNumber());
        JTextField addressField = new JTextField(20);
        addressField.setText(customer.getAddress());
        JComboBox<String> paymentPrefCombo = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash"});
        if (customer.getPaymentPreference() != null) {
            paymentPrefCombo.setSelectedItem(customer.getPaymentPreference());
        }

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Payment Preference:"), gbc);
        gbc.gridx = 1;
        formPanel.add(paymentPrefCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(200, 50, 50));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String paymentPref = (String) paymentPrefCombo.getSelectedItem();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            customer.setName(name);
            customer.setPhoneNumber(phone);
            customer.setAddress(address);
            customer.updatePaymentPreference(paymentPref);

            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        cancelBtn.addActionListener(e -> dispose());
    }
}



