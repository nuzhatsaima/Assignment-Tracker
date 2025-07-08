package org.app.test;

public class EmailTest {
    public static void main(String[] args) {
        try {
            // Try to load JavaMail classes
            Class.forName("javax.mail.Session");
            Class.forName("javax.mail.internet.MimeMessage");
            System.out.println("✅ JavaMail is available!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JavaMail is NOT available: " + e.getMessage());
            System.out.println("Please reload your Maven dependencies.");
        }
    }
}
