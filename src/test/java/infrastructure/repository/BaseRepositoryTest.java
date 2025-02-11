package infrastructure.repository;

import domain.model.Category;
import domain.model.User;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaseRepositoryTest {
    private TestRepository repository;
    private User testUser;
    private Category testCategory;

    private static class TestRepository extends BaseRepository<Category> {
        @Override
        protected EntityManager getEntityManager() {
            return super.getEntityManager();
        }
    }

    @BeforeEach
    void setUp() {
        repository = new TestRepository();
        testUser = new User("Matti", "Valovirta", "matti@test.com" + System.currentTimeMillis(), "password");
        testCategory = new Category("Some category", testUser);
    }

    @Test
    void getEntityManager() {
        EntityManager em = repository.getEntityManager();
        assertNotNull(em);
        assertTrue(em.isOpen());
        em.close();
    }

    @Test
    void save() {
        UserRepository userRepo = new UserRepository();
        User savedUser = userRepo.save(testUser);
        Category savedCategory = repository.save(testCategory);
        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Some category", savedCategory.getCategoryName());
        assertEquals(testUser, savedCategory.getCreator());
    }
}
