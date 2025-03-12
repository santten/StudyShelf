package infrastructure.repository;

import domain.model.RoleType;
import domain.model.User;
import domain.model.Role;
import domain.service.PasswordService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    private static EntityManagerFactory emf;
    private UserRepository repository;
    private User testUser;
    private RoleRepository roleRepo;
    private Role testRole;


    @BeforeAll
    static void setupDatabase() {
        emf = TestPersistenceUtil.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        repository = new UserRepository(emf);
        roleRepo = new RoleRepository(emf);

        Role testRole = roleRepo.findByName(RoleType.STUDENT);
        if (testRole == null) {
            testRole = new Role(RoleType.STUDENT);
            testRole = roleRepo.save(testRole);
        }

        testUser = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password", testRole);
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

    @Test
    void testFindByEmail() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            RoleRepository roleRepo = new RoleRepository(emf);
            Role role = roleRepo.findByName(RoleType.STUDENT);
            if (role == null) {
                role = new Role(RoleType.STUDENT);
                role = roleRepo.save(role);
            }

            String uniqueEmail = "test.user" + System.currentTimeMillis() + "@example.com";
            User testUser = new User("Test", "User", uniqueEmail, "password", role);
            testUser = repository.save(testUser);

            em.flush();
            tx.commit();

            em.clear();

            User foundByEmail = repository.findByEmail(uniqueEmail);
            assertNotNull(foundByEmail, "Should find user by email");
            assertEquals(testUser.getUserId(), foundByEmail.getUserId());
            assertEquals(uniqueEmail, foundByEmail.getEmail());

            User notFound = repository.findByEmail("nonexistent" + System.currentTimeMillis() + "@example.com");
            assertNull(notFound, "Should return null for non-existent email");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }


    @Test
    void testFindAll() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            RoleRepository roleRepo = new RoleRepository(emf);
            Role role = roleRepo.findByName(RoleType.STUDENT);
            if (role == null) {
                role = new Role(RoleType.STUDENT);
                role = roleRepo.save(role);
            }

            String uniquePrefix = "FindAll" + System.currentTimeMillis();

            List<User> initialUsers = repository.findAll();
            int initialCount = initialUsers.size();

            User user1 = new User("First", "User", uniquePrefix + "1@example.com", "password", role);
            User user2 = new User("Second", "User", uniquePrefix + "2@example.com", "password", role);
            User user3 = new User("Third", "User", uniquePrefix + "3@example.com", "password", role);

            repository.save(user1);
            repository.save(user2);
            repository.save(user3);

            em.flush();

            tx.commit();

            em.clear();

            UserRepository freshRepo = new UserRepository(emf);
            List<User> allUsers = freshRepo.findAll();

            assertNotNull(allUsers);
            assertEquals(initialCount + 3, allUsers.size(),
                    "Should find all users including the 3 newly added (initial: " + initialCount + ")");

            List<String> allEmails = allUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            assertTrue(allEmails.contains(user1.getEmail()), "Results should include user1");
            assertTrue(allEmails.contains(user2.getEmail()), "Results should include user2");
            assertTrue(allEmails.contains(user3.getEmail()), "Results should include user3");
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Test
    void testDefaultConstructor() {
        UserRepository defaultRepo = new UserRepository();
        assertNotNull(defaultRepo, "Default constructor should create a non-null repository");

        List<User> users = defaultRepo.findAll();
        assertNotNull(users, "Repository created with default constructor should work");
    }

    @Test
    void testUpdateUserFirstName() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            User savedUser = repository.save(testUser);
            em.flush();
            tx.commit();

            repository.updateUserFirstName(savedUser.getUserId(), "NewFirstName");

            User updatedUser = repository.findById(savedUser.getUserId());
            assertNotNull(updatedUser);
            assertEquals("NewFirstName", updatedUser.getFirstName());
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Test
    void testUpdateUserLastName() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            User savedUser = repository.save(testUser);
            em.flush();
            tx.commit();

            repository.updateUserLastName(savedUser.getUserId(), "NewLastName");

            User updatedUser = repository.findById(savedUser.getUserId());
            assertNotNull(updatedUser);
            assertEquals("NewLastName", updatedUser.getLastName());
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Test
    void testUpdateUserEmail() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            User savedUser = repository.save(testUser);
            em.flush();
            tx.commit();

            repository.updateUserEmail(savedUser.getUserId(), "new@example.com");

            User updatedUser = repository.findById(savedUser.getUserId());
            assertNotNull(updatedUser);
            assertEquals("new@example.com", updatedUser.getEmail());
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @AfterAll
    static void tearDown() {
        TestPersistenceUtil.closeEntityManagerFactory();
    }
}