package domain.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Service class for handling password hashing and verification.
 * Uses BCrypt algorithm for secure password encryption.
 */
public class PasswordService {
    /** Password encoder using BCrypt hashing algorithm */
    private final BCryptPasswordEncoder hashingPasswordEncoder = new BCryptPasswordEncoder();

    /**
     * Hashes a plain-text password using BCrypt.
     *
     * @param password the raw password to hash
     * @return the hashed password
     */
    public String hashPassword(String password) {
        return hashingPasswordEncoder.encode(password);
    }

    /**
     * Verifies a raw password against a hashed password.
     *
     * @param password       the raw password input
     * @param hashedPassword the hashed password to compare with
     * @return true if the password matches the hash, false otherwise
     */
    public boolean checkPassword(String password, String hashedPassword) {
        return hashingPasswordEncoder.matches(password, hashedPassword);
    }
}
