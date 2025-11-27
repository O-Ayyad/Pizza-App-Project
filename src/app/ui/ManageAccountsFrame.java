package app.ui;

import app.database.DataStore;
import app.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageAccountsFrame extends JFrame {
    private ManagerAccount manager;

    public ManageAccountsFrame(ManagerAccount manager) {
        this.manager = manager;
        setTitle("Manage Accounts");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 245, 230));

        JLabel header = new JLabel("Manage Staff Accounts", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setForeground(new Color(200, 50, 50));
        mainPanel.add(header, BorderLayout.NORTH);

        String[] columnNames = {"Username", "Account Type"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Account acc : DataStore.accounts) {
            String type = acc instanceof ManagerAccount ? "Manager" : 
                         acc instanceof StaffAccount ? "Staff" : "Customer";
            if (!"Customer".equals(type)) {
                model.addRow(new Object[]{acc.getUsername(), type});
            }
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 245, 230));

        JButton addStaffBtn = new JButton("Add Staff");
        addStaffBtn.setBackground(new Color(200, 50, 50));
        addStaffBtn.setForeground(Color.WHITE);
        addStaffBtn.setFocusPainted(false);

        JButton removeBtn = new JButton("Remove Staff");
        removeBtn.setBackground(new Color(200, 50, 50));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Color.GRAY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);

        buttonPanel.add(addStaffBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addStaffBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Add Staff Account", true);
            dialog.setSize(400, 250);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField usernameField = new JTextField(15);
            JPasswordField passwordField = new JPasswordField(15);

            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1;
            panel.add(usernameField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            panel.add(passwordField, gbc);

            JButton createBtn = new JButton("Create");
            createBtn.setBackground(new Color(200, 50, 50));
            createBtn.setForeground(Color.WHITE);
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setBackground(Color.GRAY);
            cancelBtn.setForeground(Color.WHITE);

            JPanel btnPanel = new JPanel(new FlowLayout());
            btnPanel.add(createBtn);
            btnPanel.add(cancelBtn);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            panel.add(btnPanel, gbc);

            dialog.add(panel);

            createBtn.addActionListener(e2 -> {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (DataStore.findAccount(username) != null) {
                    JOptionPane.showMessageDialog(dialog, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StaffAccount staff = new StaffAccount(username, password);
                DataStore.addAccount(staff);
                model.addRow(new Object[]{username, "Staff"});
                JOptionPane.showMessageDialog(dialog, "Staff account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            });

            cancelBtn.addActionListener(e2 -> dialog.dispose());
            dialog.setVisible(true);
        });

        removeBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an account to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String username = (String) model.getValueAt(selectedRow, 0);
            String type = (String) model.getValueAt(selectedRow, 1);

            if ("Manager".equals(type)) {
                JOptionPane.showMessageDialog(this, "Cannot remove manager accounts.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove " + username + "?", 
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                Account acc = DataStore.findAccount(username);
                if (acc != null) {
                    DataStore.accounts.remove(acc);
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Account removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        closeBtn.addActionListener(e -> dispose());
    }
}



