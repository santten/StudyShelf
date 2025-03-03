package domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordServiceTest {
    private PasswordService passwordService = new PasswordService();

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    @Test
    void testHashPassword() {
        String rawPassword = "mytest123password";
        String hashedPassword = passwordService.hashPassword(rawPassword);

        assertNotNull(hashedPassword);
        assertTrue(hashedPassword.length() > 20);
        assertTrue(passwordService.checkPassword(rawPassword, hashedPassword));
    }

    @Test
    void testCheckPassword() {
        String rawPassword = "password123test";
        String hashedPassword = passwordService.hashPassword(rawPassword);

        assertTrue(passwordService.checkPassword(rawPassword, hashedPassword));
        assertFalse(passwordService.checkPassword("wrongpassword", hashedPassword));
    }

    @Test
    void testHashPasswordDifferentEachTime() {
        String password = "samepassword";

        String hash1 = passwordService.hashPassword(password);
        String hash2 = passwordService.hashPassword(password);

        assertNotEquals(hash1, hash2);
    }
}

