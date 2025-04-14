package domain.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import javax.crypto.SecretKey;

/**
 * Service class responsible for creating and parsing JSON Web Tokens (JWT).
 * Tokens are used to authenticate users based on email identity.
 */
public class JWTService {
    /** Token expiration time (7 days in milliseconds) */
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    /** Secret key used to sign the JWT tokens */
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Creates a JWT token based on the user's email.
     *
     * @param email the user's email to encode in the token
     * @return a signed JWT token
     */
    public String createToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the email (subject) from a given JWT token.
     *
     * @param token the JWT token
     * @return the email contained in the token
     */
    public String getEmailFromToken(String token) {
        return parseToken(token).getBody().getSubject();
    }

    /**
     * Parses the JWT token and returns its claims.
     *
     * @param token the JWT token
     * @return parsed claims
     */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }

    /**
     * Provides the static secret key used for signing tokens.
     *
     * @return the secret key
     */
    public static SecretKey getSecretKey() {
        return secretKey;
    }
}
