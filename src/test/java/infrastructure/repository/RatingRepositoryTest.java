package infrastructure.repository;

import domain.model.*;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RatingRepositoryTest {
    private RatingRepository repository;
    private RoleRepository roleRepo;
    private UserRepository userRepo;
    private StudyMaterialRepository materialRepo;
    private User user;
    private StudyMaterial material;
    private Rating testRating;

    @BeforeAll
    void setupDatabase() {
        repository = new RatingRepository();
        roleRepo = new RoleRepository();
        userRepo = new UserRepository();
        materialRepo = new StudyMaterialRepository();
    }

    @BeforeEach
    void setUp() {
        Role testRole = roleRepo.findByName(RoleType.STUDENT);
        if (testRole == null) {
            testRole = new Role(RoleType.STUDENT);
            testRole = roleRepo.save(testRole);
        }

        user = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password", testRole);
        user = userRepo.save(user);

        material = new StudyMaterial(user, "Java", "Best Practices",
                "link", 100.0f, "PDF", LocalDateTime.now(), MaterialStatus.APPROVED);
        material = materialRepo.save(material);

        testRating = new Rating(5, material, user);
        testRating = repository.save(testRating);
    }

    @Test
    void testSave() {
        assertNotNull(testRating);
        assertNotNull(testRating.getRatingId());
        assertEquals(5, testRating.getRatingScore());
        assertEquals(material.getMaterialId(), testRating.getStudyMaterial().getMaterialId());
        assertEquals(user.getUserId(), testRating.getUser().getUserId());
    }

    @Test
    void testFindById() {
        Rating foundRating = repository.findById(testRating.getRatingId());
        assertNotNull(foundRating);
        assertEquals(testRating.getRatingId(), foundRating.getRatingId());
        assertEquals(testRating.getRatingScore(), foundRating.getRatingScore());
        assertEquals(material.getMaterialId(), foundRating.getStudyMaterial().getMaterialId());
        assertEquals(user.getUserId(), foundRating.getUser().getUserId());
    }

    @Test
    void testFindByMaterial() {
        var ratings = repository.findByMaterial(material);
        assertNotNull(ratings);
        assertFalse(ratings.isEmpty(), "Rating list should not be empty");
        assertEquals(1, ratings.size(), "Should have exactly one rating");
        assertEquals(testRating.getRatingId(), ratings.get(0).getRatingId());
    }

    @Test
    void testFindAverageRatingByMaterial() {
        Double avgRating = repository.findAverageRatingByMaterial(material);
        assertNotNull(avgRating, "Average rating should not be null");
        assertEquals(5.0, avgRating, 0.01, "The average rating should be 5.0");

        Rating newRating = new Rating(3, material, user);
        repository.save(newRating);

        avgRating = repository.findAverageRatingByMaterial(material);
        assertEquals(4.0, avgRating, 0.01, "The new average rating should be 4.0");
    }

    @AfterAll
    void tearDown() {
        repository = null;
        roleRepo = null;
        userRepo = null;
        materialRepo = null;
    }
}
