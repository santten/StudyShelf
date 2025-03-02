package domain.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JWTServiceTest {
    private final JWTService jwtService = new JWTService();

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

        assertEquals(email, jwtService.getEmailFromToken(token));
    }

    @Test
    void testInvalidToken() {
        assertThrows(JwtException.class, () -> jwtService.getEmailFromToken("wrongtoken"));
    }

    @Test
    void testExpiredToken() {
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject("expired@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10_000))
                .setExpiration(new Date(System.currentTimeMillis() - 5_000))
                .signWith(JWTService.getSecretKey())
                .compact();
        assertThrows(ExpiredJwtException.class, () -> jwtService.getEmailFromToken(expiredToken));
    }

}

