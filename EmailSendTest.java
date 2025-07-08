package org.app.test;

import org.app.util.EmailUtil;

public class EmailSendTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing email sending...");

            // Test sending a verification email
            String testEmail = "nuzhatxx@gmail.com"; // Your own email for testing
            String testCode = "123456";

            EmailUtil.sendVerificationEmail(testEmail, testCode);

            System.out.println("✅ Email sent successfully!");
            System.out.println("Check your inbox at: " + testEmail);
            System.out.println("Expected verification code: " + testCode);

        } catch (Exception e) {
            System.out.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
