package infrastructure.repository;

import domain.model.User;
import domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    private UserRepository repository;
    private User testUser;

    @BeforeEach
    void setUp() {
        repository = new UserRepository();
        testUser = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password");
    }

    @Test
    void save() {
        User savedUser = repository.save(testUser);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserId());
        assertEquals("Armas", savedUser.getFirstName());
        assertEquals("Nevolainen", savedUser.getLastName());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void findById() {
        User savedUser = repository.save(testUser);
        User foundUser = repository.findById(savedUser.getUserId());
        assertNotNull(foundUser);
        assertEquals(savedUser.getUserId(), foundUser.getUserId());
        assertEquals(savedUser.getEmail(), foundUser.getEmail());
    }
}