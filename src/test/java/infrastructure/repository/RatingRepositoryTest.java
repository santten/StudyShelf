package infrastructure.repository;

import domain.model.*;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private EntityManagerFactory emf;
    @BeforeAll
    void setupDatabase() {
        emf = TestPersistenceUtil.getEntityManagerFactory();
        repository = new RatingRepository(emf);
        roleRepo = new RoleRepository(emf);
        userRepo = new UserRepository(emf);
        materialRepo = new StudyMaterialRepository(emf);
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

    @Test
    void testFindByUser() {
        StudyMaterial newMaterial = new StudyMaterial(user, "Python", "Python Best Practices",
                "link2", 80.0f, "PDF", LocalDateTime.now(), MaterialStatus.APPROVED);
        newMaterial = materialRepo.save(newMaterial);

        Rating secondRating = new Rating(4, newMaterial, user);
        secondRating = repository.save(secondRating);

        List<Rating> userRatings = repository.findByUser(user);

        assertNotNull(userRatings);
        assertEquals(2, userRatings.size());

        List<Integer> ratingIds = userRatings.stream()
                .map(Rating::getRatingId)
                .collect(Collectors.toList());

        assertTrue(ratingIds.contains(testRating.getRatingId()));
        assertTrue(ratingIds.contains(secondRating.getRatingId()));
    }

    @Test
    void testFindByUserAndMaterial() {
        List<Rating> ratings = repository.findByUserAndMaterial(user, material);

        assertNotNull(ratings);
        assertEquals(1, ratings.size());
        assertEquals(testRating.getRatingId(), ratings.get(0).getRatingId());
    }

    @Test
    void testHasUserReviewedMaterial_WhenReviewExists() {
        boolean hasReviewed = repository.hasUserReviewedMaterial(user, material);

        assertTrue(hasReviewed, "User should have reviewed the material");
    }

    @Test
    void testHasUserReviewedMaterial_WhenNoReviewExists() {
        User newUser = new User("Jane", "Doe", "jane" + System.currentTimeMillis() + "@example.com", "password", user.getRole());
        newUser = userRepo.save(newUser);

        boolean hasReviewed = repository.hasUserReviewedMaterial(newUser, material);

        assertFalse(hasReviewed, "New user should not have reviewed the material");
    }

    @Test
    void testFindAverageRatingByMaterial_WithMultipleRatings() {
        User anotherUser = new User("Bob", "Smith", "bob" + System.currentTimeMillis() + "@example.com", "password", user.getRole());
        anotherUser = userRepo.save(anotherUser);

        Rating anotherRating = new Rating(3, material, anotherUser);
        repository.save(anotherRating);

        Double avgRating = repository.findAverageRatingByMaterial(material);

        assertNotNull(avgRating);
        assertEquals(4.0, avgRating, 0.01, "Average of 5 and 3 should be 4.0");
    }

    @Test
    void testFindAverageRatingByMaterial_WithNoRatings() {
        StudyMaterial newMaterial = new StudyMaterial(user, "No Ratings", "A material with no ratings",
                "link3", 60.0f, "PDF", LocalDateTime.now(), MaterialStatus.APPROVED);
        newMaterial = materialRepo.save(newMaterial);

        Double avgRating = repository.findAverageRatingByMaterial(newMaterial);
        if (avgRating == null) {
            assertNull(avgRating, "Average rating should be null when no ratings exist");
        } else {
            // Other implementations might return 0.0
            assertEquals(0.0, avgRating, 0.01, "Material with no ratings should have 0.0 average");
        }
    }

    @Test
    void testDeleteById() {
        // Get the ID to delete
        int ratingId = testRating.getRatingId();

        // Delete the rating by ID
        repository.deleteById(ratingId);

        // Verify the rating is deleted
        Rating deletedRating = repository.findById(ratingId);
        assertNull(deletedRating, "Rating should be deleted");
    }

    @Test
    void testDeleteByMaterial_WithMultipleRatings() {

        User anotherUser = new User("Charlie", "Brown", "charlie" + System.currentTimeMillis() + "@example.com", "password", user.getRole());
        anotherUser = userRepo.save(anotherUser);

        Rating anotherRating = new Rating(2, material, anotherUser);
        repository.save(anotherRating);

        List<Rating> initialRatings = repository.findByMaterial(material);
        assertTrue(initialRatings.size() >= 2, "Should have at least 2 ratings for the material");


        repository.deleteByMaterial(material);

        List<Rating> remainingRatings = repository.findByMaterial(material);
        assertEquals(0, remainingRatings.size(), "All ratings for the material should be deleted");
    }


    @AfterAll
    void tearDown() {
        repository = null;
        roleRepo = null;
        userRepo = null;
        materialRepo = null;
        TestPersistenceUtil.closeEntityManagerFactory();
    }
}
