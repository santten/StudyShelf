package infrastructure.repository;

import domain.model.Category;
import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseRepositoryTest {
    private EntityManager entityManager;
    private TestRepository categoryRepository;
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    private User testUser;
    private Role testRole;
    private Category testCategory;

    // Inner class for testing
    private static class TestRepository extends BaseRepository<Category> {
        public TestRepository() {
            super(Category.class);
        }

        // Constructor for test EntityManagerFactory
        public TestRepository(EntityManagerFactory emf) {
            super(Category.class, emf);
        }
    }

    @BeforeAll
    void setupDatabase() {
        entityManager = TestPersistenceUtil.getEntityManager();

        EntityManagerFactory testEmf = TestPersistenceUtil.getEntityManagerFactory();
        categoryRepository = new TestRepository(testEmf);
        roleRepository = new RoleRepository(testEmf);
        userRepository = new UserRepository(testEmf);
    }

    @BeforeEach
    void setUp() {
        entityManager.clear(); // Reset before each test

        testRole = roleRepository.findByName(RoleType.TEACHER);
        if (testRole == null) {
            testRole = new Role(RoleType.TEACHER);
            testRole = roleRepository.save(testRole);
        }

        testUser = new User("Matti", "Valovirta", "matti" + System.currentTimeMillis() + "@test.com", "password", testRole);
        testUser = userRepository.save(testUser);

        testCategory = new Category("Some category", testUser);
    }

    @Test
    void testSave() {
        Category savedCategory = categoryRepository.save(testCategory);

        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Some category", savedCategory.getCategoryName());
        assertEquals(testUser, savedCategory.getCreator());
    }

    @Test
    void testFindById() {
        Category savedCategory = categoryRepository.save(testCategory);
        Category foundCategory = categoryRepository.findById(savedCategory.getCategoryId());

        assertNotNull(foundCategory);
        assertEquals(savedCategory.getCategoryId(), foundCategory.getCategoryId());
    }

    @Test
    void testFindAll() {
        categoryRepository.save(testCategory);

        List<Category> categories = categoryRepository.findAll();
        assertFalse(categories.isEmpty());
    }

    @Test
    void testUpdate() {
        Category savedCategory = categoryRepository.save(testCategory);

        savedCategory.setCategoryName("Updated Category");
        Category updatedCategory = categoryRepository.update(savedCategory);

        assertNotNull(updatedCategory);
        assertEquals("Updated Category", updatedCategory.getCategoryName());
    }

    @Test
    void testDelete() {
        Category savedCategory = categoryRepository.save(testCategory);

        categoryRepository.delete(savedCategory);

        entityManager.clear(); // Ensure fresh query
        Category deletedCategory = categoryRepository.findById(savedCategory.getCategoryId());

        assertNull(deletedCategory);
    }

    @AfterAll
    void tearDown() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
        TestPersistenceUtil.closeEntityManagerFactory(); // Add this to clean up
    }
}
