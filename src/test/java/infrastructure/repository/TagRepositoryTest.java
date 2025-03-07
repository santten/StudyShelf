package infrastructure.repository;

import domain.model.RoleType;
import domain.model.Tag;
import domain.model.User;
import domain.model.Role;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.TestPersistenceUtil;

import static org.junit.jupiter.api.Assertions.*;

class TagRepositoryTest {
    private static EntityManagerFactory emf;
    private TagRepository repository;
    private User creator;
    private Tag testTag;
    private RoleRepository roleRepo;
    private UserRepository userRepo;

    @BeforeAll
    static void setupDatabase() {
        emf = TestPersistenceUtil.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        repository = new TagRepository(emf);
        roleRepo = new RoleRepository(emf);
        userRepo = new UserRepository(emf);

        Role testRole = roleRepo.findByName(RoleType.STUDENT);
        if (testRole == null) {
            testRole = new Role(RoleType.STUDENT);
            testRole = roleRepo.save(testRole);
        }

        creator = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password", testRole);
        creator = userRepo.save(creator);

        testTag = new Tag("Java" + System.currentTimeMillis(), creator);
    }

    @Test
    void save() {
        Tag savedTag = repository.save(testTag);
        assertNotNull(savedTag);
        assertNotNull(savedTag.getTagId());
        assertEquals(testTag.getTagName(), savedTag.getTagName());
        assertEquals(creator.getUserId(), savedTag.getCreator().getUserId());
    }

    @Test
    void findById() {
        Tag savedTag = repository.save(testTag);
        Tag foundTag = repository.findById(savedTag.getTagId());
        assertNotNull(foundTag);
        assertEquals(savedTag.getTagId(), foundTag.getTagId());
        assertEquals(savedTag.getTagName(), foundTag.getTagName());
        assertEquals(creator.getUserId(), foundTag.getCreator().getUserId());
    }
    @AfterAll
    static void tearDown() {
        TestPersistenceUtil.closeEntityManagerFactory();
    }
}