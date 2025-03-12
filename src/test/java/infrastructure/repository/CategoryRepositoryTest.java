package infrastructure.repository;

import domain.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;

import java.time.LocalDateTime;
import java.util.List;

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



    @Test
    void testCountMaterialsByCategory() {
        entityManager.getTransaction().begin();
        Category savedCategory = repository.save(testCategory);

        // Create and save study materials for this category
        StudyMaterialRepository materialRepository = new StudyMaterialRepository(TestPersistenceUtil.getEntityManagerFactory());

        StudyMaterial material1 = new StudyMaterial(
                creator,
                "Java Basics",
                "Introduction to Java",
                "materials/java-basics.pdf",
                1.5f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.APPROVED
        );
        material1.setCategory(savedCategory);
        materialRepository.save(material1);

        StudyMaterial material2 = new StudyMaterial(
                creator,
                "Advanced Java",
                "Advanced Java concepts",
                "materials/advanced-java.pdf",
                2.5f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.PENDING
        );
        material2.setCategory(savedCategory);
        materialRepository.save(material2);

        entityManager.getTransaction().commit();

        // Test countMaterialsByCategory
        long count = repository.countMaterialsByCategory(savedCategory);

        // Verify results
        assertEquals(2, count);
    }


    @Test
    void testCountPendingMaterialsByCategory() {
        entityManager.getTransaction().begin();
        Category savedCategory = repository.save(testCategory);

        // Create and save study materials for this category
        StudyMaterialRepository materialRepository = new StudyMaterialRepository(TestPersistenceUtil.getEntityManagerFactory());

        StudyMaterial material1 = new StudyMaterial(
                creator,
                "Java Basics",
                "Introduction to Java",
                "materials/java-basics.pdf",
                1.5f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.APPROVED
        );
        material1.setCategory(savedCategory);
        materialRepository.save(material1);

        StudyMaterial material2 = new StudyMaterial(
                creator,
                "Advanced Java",
                "Advanced Java concepts",
                "materials/advanced-java.pdf",
                2.5f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.PENDING
        );
        material2.setCategory(savedCategory);
        materialRepository.save(material2);

        StudyMaterial material3 = new StudyMaterial(
                creator,
                "Java Debugging",
                "Debugging Java applications",
                "materials/debugging.pdf",
                3.0f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.PENDING
        );
        material3.setCategory(savedCategory);
        materialRepository.save(material3);

        entityManager.getTransaction().commit();

        // Test countPendingMaterialsByCategory
        long pendingCount = repository.countPendingMaterialsByCategory(savedCategory);

        // Verify results
        assertEquals(2, pendingCount);
    }

    @Test
    void testUpdateCategoryTitle() {
        entityManager.getTransaction().begin();
        Category savedCategory = repository.save(testCategory);
        int categoryId = savedCategory.getCategoryId();
        entityManager.getTransaction().commit();

        // Update title
        String newTitle = "Updated Java Category";
        entityManager.getTransaction().begin();
        repository.updateCategoryTitle(categoryId, newTitle);
        entityManager.getTransaction().commit();

        // Verify the update
        Category updatedCategory = repository.findById(categoryId);
        assertEquals(newTitle, updatedCategory.getCategoryName());
    }

//    @Test
//    void testCategoryRepository_Constructor() {
//        // Test both constructors
//        CategoryRepository defaultRepo = new CategoryRepository();
//        assertNotNull(defaultRepo, "Default constructor should create a valid repository");
//
//        // The parameterized constructor is already tested throughout the test class
//        assertNotNull(repository, "Parameterized constructor should create a valid repository");
//    }


    @AfterAll
    void tearDown() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
        TestPersistenceUtil.closeEntityManagerFactory();
    }
}