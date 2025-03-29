package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * u5bc6u78bcu52a0u5bc6u6e2cu8a66u985e
 * u4f7fu7528u65b9u5f0fuff1au57f7u884cu6b64u6e2cu8a66u985eu4e26u67e5u770bu63a7u5236u53f0u8f38u51fau7684u52a0u5bc6u5bc6u78bc
 * u5c07u52a0u5bc6u5f8cu7684u5bc6u78bcu5b58u5165u8cc7u6599u5eabu4e2d
 */
public class PasswordEncoderTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void encodePassword() {
        // u8981u52a0u5bc6u7684u539fu59cbu5bc6u78bc
        String rawPassword1 = "admin";
        String rawPassword2 = "user";

        // u52a0u5bc6u5bc6u78bc
        String encodedPassword1 = passwordEncoder.encode(rawPassword1);
        String encodedPassword2 = passwordEncoder.encode(rawPassword2);

        // u8f38u51fau52a0u5bc6u5f8cu7684u5bc6u78bc
        System.out.println("Encoded admin password: " + encodedPassword1);
        System.out.println("Encoded user password: " + encodedPassword2);

        // u9a57u8b49u5bc6u78bcu662fu5426u6b63u78ba
        boolean isMatch1 = passwordEncoder.matches(rawPassword1, encodedPassword1);
        boolean isMatch2 = passwordEncoder.matches(rawPassword2, encodedPassword2);

        System.out.println("Password 'admin' matches: " + isMatch1);
        System.out.println("Password 'user' matches: " + isMatch2);
        
        // u6aa2u67e5u9810u8a2du5bc6u78bcu662fu5426u5339u914d
        String storedAdminPassword = "$2a$10$ixlPY3AAd4ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWNip/Lna6JwC";
        String storedUserPassword = "$2a$10$teJrCEnsxNT49ZpXU7n22O27aCGbVYYe/RG6/XxdWPJbOLZYf.9Vy";
        
        boolean isStoredAdminMatch = passwordEncoder.matches(rawPassword1, storedAdminPassword);
        boolean isStoredUserMatch = passwordEncoder.matches(rawPassword2, storedUserPassword);
        
        System.out.println("\nu6aa2u67e5u9810u8a2du5bc6u78bcu662fu5426u5339u914d:");
        System.out.println("Stored admin password matches: " + isStoredAdminMatch);
        System.out.println("Stored user password matches: " + isStoredUserMatch);
    }

    @Test
    public void encodeCustomPassword() {
        // u5728u9019u88e1u8f38u5165u60a8u60f3u8981u52a0u5bc6u7684u5bc6u78bc
        String customPassword = "admin";

        // u52a0u5bc6u5bc6u78bc
        String encodedPassword = passwordEncoder.encode(customPassword);

        // u8f38u51fau52a0u5bc6u5f8cu7684u5bc6u78bc
        System.out.println("Encoded custom password: " + encodedPassword);

        // u9a57u8b49u5bc6u78bcu662fu5426u6b63u78ba
        boolean isMatch = passwordEncoder.matches(customPassword, encodedPassword);
        System.out.println("Custom password matches: " + isMatch);
        
        // u751fu6210 SQL u63d2u5165u8a9eu53e5
        System.out.println("\nSQL INSERT u8a9eu53e5:");
        System.out.println("INSERT INTO users (username, password, role) VALUES ('admin', '" + encodedPassword + "', 'ADMIN') ON CONFLICT (username) DO NOTHING;");
    }
}
