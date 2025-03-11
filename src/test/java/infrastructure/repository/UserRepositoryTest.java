package infrastructure.repository;

import domain.model.User;
import domain.model.Role;
import domain.model.RoleType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {
    private static EntityManagerFactory emf;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private User testUser;

    @BeforeAll
    static void setupDatabase() {
        emf = TestPersistenceUtil.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository(emf);
        roleRepository = new RoleRepository(emf);

        Role testRole = roleRepository.findByName(RoleType.STUDENT);
        if (testRole == null) {
            testRole = new Role(RoleType.STUDENT);
            testRole = roleRepository.save(testRole);
        }

        testUser = new User("John", "Doe", "john.doe@example.com", "password123", testRole);
        userRepository.save(testUser);
    }

    @Test
    void testSaveUser() {
        User savedUser = userRepository.save(new User("Alice", "Smith", "alice@example.com", "password123", testUser.getRole()));
        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserId());
        assertEquals("Alice", savedUser.getFirstName());
        assertEquals("Smith", savedUser.getLastName());
    }

    @Test
    void testFindById() {
        User foundUser = userRepository.findById(testUser.getUserId());
        assertNotNull(foundUser);
        assertEquals(testUser.getUserId(), foundUser.getUserId());
    }

    @Test
    void testFindByEmail() {
        User foundUser = userRepository.findByEmail("john.doe@example.com");
        assertNotNull(foundUser);
        assertEquals(testUser.getUserId(), foundUser.getUserId());
    }

    @Test
    void testFindAllUsers() {
        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty());
        assertEquals(2, users.size());
    }

    @Test
    void testUpdateUser() {
        testUser.setFirstName("UpdatedName");
        User updatedUser = userRepository.update(testUser);
        assertNotNull(updatedUser);
        assertEquals("UpdatedName", updatedUser.getFirstName());
    }

    @Test
    void testUpdateUserFields() {
        User updatedUser = userRepository.updateUserFields(testUser.getUserId(), "NewFirst", "NewLast", "new.email@example.com");
        assertNotNull(updatedUser);
        assertEquals("NewFirst", updatedUser.getFirstName());
        assertEquals("NewLast", updatedUser.getLastName());
        assertEquals("new.email@example.com", updatedUser.getEmail());
    }

    @Test
    void testUpdateUserPassword() {
        userRepository.updateUserPassword(testUser.getUserId(), "newPassword123");
        User updatedUser = userRepository.findById(testUser.getUserId());
        assertNotNull(updatedUser);
        assertEquals("newPassword123", updatedUser.getPassword());
    }

    @Test
    void testDeleteUser() {
        boolean deleted = userRepository.deleteById(testUser.getUserId());
        assertTrue(deleted);
        assertNull(userRepository.findById(testUser.getUserId()));
    }

    @AfterEach
    void cleanUp() {
        userRepository.delete(testUser);
    }

    @AfterAll
    static void tearDown() {
        TestPersistenceUtil.closeEntityManagerFactory();
    }
}
