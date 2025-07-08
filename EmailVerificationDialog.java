package org.app.gui;

import org.app.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmailVerificationDialog extends JDialog {
    private UserService userService;
    private String userEmail;
    private JTextField codeField;
    private boolean verified = false;

    public EmailVerificationDialog(JFrame parent, UserService userService, String email) {
        super(parent, "Email Verification", true);
        this.userService = userService;
        this.userEmail = email;

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        codeField = new JTextField(10);
        codeField.setFont(new Font("Arial", Font.PLAIN, 16));
        codeField.setHorizontalAlignment(JTextField.CENTER);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(33, 150, 243));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Email Verification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Info message
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><center>A verification code has been sent to:<br><b>" + userEmail + "</b><br><br>Please enter the 6-digit code below:</center></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(infoLabel, gbc);

        // Code input
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Verification Code:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(codeField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton verifyButton = new JButton("Verify");
        verifyButton.setBackground(new Color(76, 175, 80));
        verifyButton.setForeground(Color.WHITE);
        verifyButton.addActionListener(e -> handleVerification());

        JButton resendButton = new JButton("Resend Code");
        resendButton.setBackground(new Color(255, 152, 0));
        resendButton.setForeground(Color.WHITE);
        resendButton.addActionListener(e -> handleResendCode());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(verifyButton);
        buttonPanel.add(resendButton);
        buttonPanel.add(cancelButton);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        codeField.addActionListener(e -> handleVerification());
    }

    private void handleVerification() {
        String code = codeField.getText().trim();

        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the verification code.",
                    "Verification Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userService.verifyEmail(userEmail, code)) {
            verified = true;
            JOptionPane.showMessageDialog(this,
                    "Email verified successfully!",
                    "Verification Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid verification code. Please try again.",
                    "Verification Failed",
                    JOptionPane.ERROR_MESSAGE);
            codeField.setText("");
            codeField.requestFocus();
        }
    }

    private void handleResendCode() {
        userService.resendVerificationCode(userEmail);
        JOptionPane.showMessageDialog(this,
                "A new verification code has been sent to your email.",
                "Code Resent",
                JOptionPane.INFORMATION_MESSAGE);
        codeField.setText("");
        codeField.requestFocus();
    }

    public boolean isVerified() {
        return verified;
    }
}
