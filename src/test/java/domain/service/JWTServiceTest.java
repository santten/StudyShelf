package domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JWTServiceTest {
    private JWTService jwtService = new JWTService();

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
    }

    @Test
    void testCreateToken() {
        String email = "test@example.com";
        String token = jwtService.createToken(email);

        assertNotNull(token);
    }

    @Test
    void testGetEmailFromToken() {
        String email = "test@example.com";
        String token = jwtService.createToken(email);

        String extractedEmail = jwtService.getEmailFromToken(token);
        assertEquals(email, extractedEmail);
    }

    @Test
    void testInvalidToken() {
        JWTService jwtService = new JWTService();
        String token = jwtService.createToken("test@example.com");
        assertThrows(Exception.class, () -> jwtService.getEmailFromToken("wrongtoken"));
    }
}

