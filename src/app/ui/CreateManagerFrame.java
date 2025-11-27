package app.ui;

import app.database.DataStore;
import app.models.ManagerAccount;

import javax.swing.*;
import java.awt.*;

public class CreateManagerFrame extends JFrame {

    public CreateManagerFrame() {
        setTitle("Create Initial Manager Account");
        setSize(450, 280);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Explanation label at the top
        JLabel explanationLabel = new JLabel("<html><center>No manager accounts exist yet.<br>" +
                "You need to create a manager account to access the system.</center></html>");
        explanationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        explanationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(explanationLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Manager Username:");
        JTextField usernameField = new JTextField();
        usernameField.setToolTipText("Enter a unique username for the manager");

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setToolTipText("Enter a secure password");

        JButton createBtn = new JButton("Create Manager");

        // Positioning components
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(createBtn, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Button action
        createBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if(username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ManagerAccount acc = new ManagerAccount(username, password);
            DataStore.addAccount(acc);

            JOptionPane.showMessageDialog(this,
                    "Manager account successfully created!\nYou can now log in and manage the system.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}
