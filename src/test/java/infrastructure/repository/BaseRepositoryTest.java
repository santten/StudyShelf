package infrastructure.repository;

import domain.model.Category;
import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaseRepositoryTest {
    private TestRepository repository;
    private User testUser;
    private Category testCategory;
    private RoleRepository roleRepo;
    private UserRepository userRepo;


    private static class TestRepository extends BaseRepository<Category> {
        public TestRepository() {
            super(Category.class);
        }

        @Override
        protected EntityManager getEntityManager() {
            return super.getEntityManager();
        }
    }

//    @BeforeEach
//    void setUp() {
//        repository = new TestRepository();
//        Role testRole = new Role(RoleType.TEACHER);
//        testUser = new User("Matti", "Valovirta", "matti@test.com" + System.currentTimeMillis(), "password",testRole );
//        testCategory = new Category("Some category", testUser);
//    }
    @BeforeEach
    void setUp() {
        repository = new TestRepository();
        roleRepo = new RoleRepository();
        userRepo = new UserRepository();

        Role testRole = roleRepo.findByName(RoleType.TEACHER);
        if (testRole == null) {
            testRole = new Role(RoleType.TEACHER);
            testRole = roleRepo.save(testRole);
        }

        testUser = new User("Matti", "Valovirta", "matti" + System.currentTimeMillis() + "@test.com", "password", testRole);
        testCategory = new Category("Some category", testUser);
    }

    @Test
    void getEntityManager() {
        EntityManager em = repository.getEntityManager();
        assertNotNull(em);
        assertTrue(em.isOpen());
        em.close();
    }

//    @Test
//    void save() {
//        UserRepository userRepo = new UserRepository();
//        User savedUser = userRepo.save(testUser);
//        Category savedCategory = repository.save(testCategory);
//        assertNotNull(savedCategory);
//        assertNotNull(savedCategory.getCategoryId());
//        assertEquals("Some category", savedCategory.getCategoryName());
//        assertEquals(testUser, savedCategory.getCreator());
//    }

    @Test
    void save() {
        User savedUser = userRepo.save(testUser);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserId());

        testCategory.setCreator(savedUser);

        Category savedCategory = repository.save(testCategory);
        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Some category", savedCategory.getCategoryName());
        assertEquals(savedUser, savedCategory.getCreator());
    }
}
