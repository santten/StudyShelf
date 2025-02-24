package domain.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordService {
    private final BCryptPasswordEncoder hashingPasswordEncoder = new BCryptPasswordEncoder();

    public String hashPassword(String password) {
        return hashingPasswordEncoder.encode(password);
    }

    public boolean checkPassword(String password, String hashedPassword) {
        return hashingPasswordEncoder.matches(password, hashedPassword);
    }
}
