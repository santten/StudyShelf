package domain.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

public class JWTService {
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
