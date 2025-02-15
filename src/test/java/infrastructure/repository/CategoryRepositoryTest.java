package infrastructure.repository;

import domain.model.Category;
import domain.model.RoleType;
import domain.model.User;
import domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryRepositoryTest {
    private CategoryRepository repository;
    private User creator;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        repository = new CategoryRepository();
        UserRepository userRepo = new UserRepository();
        Role testRole = new Role(RoleType.STUDENT);
        creator = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password", testRole);
        creator = userRepo.save(creator);
        testCategory = new Category("Java", creator);
    }

    @Test
    void save() {
        Category savedCategory = repository.save(testCategory);
        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Java", savedCategory.getCategoryName());
        assertEquals(creator, savedCategory.getCreator());
    }

    @Test
    void findById() {
        Category savedCategory = repository.save(testCategory);
        Category foundCategory = repository.findById(savedCategory.getCategoryId());
        assertNotNull(foundCategory);
        assertEquals(savedCategory.getCategoryId(), foundCategory.getCategoryId());
        assertEquals(savedCategory.getCategoryName(), foundCategory.getCategoryName());
        assertEquals(creator.getUserId(), foundCategory.getCreator().getUserId());
    }
}
