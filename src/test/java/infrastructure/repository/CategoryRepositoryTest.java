package infrastructure.repository;

import domain.model.Category;
import domain.model.RoleType;
import domain.model.User;
import domain.model.Role;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryRepositoryTest {
    private EntityManager entityManager;
    private CategoryRepository repository;
    private RoleRepository roleRepo;
    private UserRepository userRepo;
    private User creator;
    private Category testCategory;

    @BeforeAll
    void setupDatabase() {
        entityManager = TestPersistenceUtil.getEntityManager();

        EntityManagerFactory testEmf = TestPersistenceUtil.getEntityManagerFactory();
        repository = new CategoryRepository(testEmf);
        roleRepo = new RoleRepository(testEmf);
        userRepo = new UserRepository(testEmf);
    }

    @BeforeEach
    void setUp() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            Role testRole = roleRepo.findByName(RoleType.STUDENT);
            if (testRole == null) {
                testRole = new Role(RoleType.STUDENT);
                testRole = roleRepo.save(testRole);
            }

            creator = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password", testRole);
            creator = userRepo.save(creator);

            testCategory = new Category("Java", creator);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Test
    void testSave() {
        entityManager.getTransaction().begin();
        Category savedCategory = repository.save(testCategory);
        entityManager.getTransaction().commit();

        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Java", savedCategory.getCategoryName());
        assertEquals(creator, savedCategory.getCreator());
    }

    @Test
    void testFindById() {
        entityManager.getTransaction().begin();
        Category savedCategory = repository.save(testCategory);
        entityManager.getTransaction().commit();

        Category foundCategory = repository.findById(savedCategory.getCategoryId());

        assertNotNull(foundCategory);
        assertEquals(savedCategory.getCategoryId(), foundCategory.getCategoryId());
        assertEquals(savedCategory.getCategoryName(), foundCategory.getCategoryName());
        assertEquals(creator.getUserId(), foundCategory.getCreator().getUserId());
    }

    @Test
    void testFindByName() {
        entityManager.getTransaction().begin();
        repository.save(testCategory);
        entityManager.getTransaction().commit();

        var categories = repository.findByName("Java");
        assertFalse(categories.isEmpty());
        assertTrue(categories.get(0).getCategoryName().contains("Java"));
    }

    @Test
    void testFindCategoriesByUser() {
        entityManager.getTransaction().begin();
        repository.save(testCategory);
        entityManager.getTransaction().commit();

        var categories = repository.findCategoriesByUser(creator);
        assertFalse(categories.isEmpty());
        assertEquals("Java", categories.get(0).getCategoryName());

        assertEquals(creator.getUserId(), categories.get(0).getCreator().getUserId());
    }


    @Test
    void testUpdate() {
        entityManager.getTransaction().begin();
        Category savedCategory = repository.save(testCategory);
        entityManager.getTransaction().commit();

        savedCategory.setCategoryName("Updated Java");
        entityManager.getTransaction().begin();
        Category updatedCategory = repository.update(savedCategory);
        entityManager.getTransaction().commit();

        assertNotNull(updatedCategory);
        assertEquals("Updated Java", updatedCategory.getCategoryName());
    }

    @Test
    void testDelete() {
        entityManager.getTransaction().begin();
        Category savedCategory = repository.save(testCategory);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        repository.delete(savedCategory);
        entityManager.getTransaction().commit();

        Category deletedCategory = repository.findById(savedCategory.getCategoryId());

        assertNull(deletedCategory);
    }

    @AfterAll
    void tearDown() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
        TestPersistenceUtil.closeEntityManagerFactory();
    }
}