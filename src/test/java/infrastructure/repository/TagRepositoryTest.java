package infrastructure.repository;

import domain.model.RoleType;
import domain.model.Tag;
import domain.model.User;
import domain.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TagRepositoryTest {
    private TagRepository repository;
    private User creator;
    private Tag testTag;
    private RoleRepository roleRepo;
    private UserRepository userRepo;

//    @BeforeEach
//    void setUp() {
//        repository = new TagRepository();
//        Role testRole = new Role(RoleType.STUDENT);
//        UserRepository userRepo = new UserRepository();
//        creator = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password", testRole);
//        creator = userRepo.save(creator);
//
//        testTag = new Tag("Java" + System.currentTimeMillis(), creator);
//    }

    @BeforeEach
    void setUp() {
        repository = new TagRepository();
        roleRepo = new RoleRepository();
        userRepo = new UserRepository();

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
}