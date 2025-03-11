package infrastructure.repository;

import domain.model.*;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewRepositoryTest {
    private ReviewRepository repository;
    private RoleRepository roleRepo;
    private UserRepository userRepo;
    private CategoryRepository categoryRepo;
    private StudyMaterialRepository materialRepo;
    private User user;
    private StudyMaterial material;
    private Review review;
    private EntityManagerFactory emf;

    @BeforeAll
    void setupDatabase() {
        emf = TestPersistenceUtil.getEntityManagerFactory();
        repository = new ReviewRepository(emf);
        roleRepo = new RoleRepository(emf);
        userRepo = new UserRepository(emf);
        categoryRepo = new CategoryRepository(emf);
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

        Category category = new Category("Java", user);
        category = categoryRepo.save(category);

        material = new StudyMaterial(
                user,
                "Java for dummies",
                "Introduction to Java Programming for dummies",
                "materials/java-dumb.pdf",
                10f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.PENDING
        );
        material.setCategory(category);
        material = materialRepo.save(material);

        review = new Review("Great!!!", material, user);
        review = repository.save(review);
    }

    @Test
    void testSave() {
        assertNotNull(review);
        assertNotNull(review.getReviewId());
        assertEquals("Great!!!", review.getReviewText());
        assertEquals(material.getMaterialId(), review.getStudyMaterial().getMaterialId());
        assertEquals(user.getUserId(), review.getUser().getUserId());
    }

    @Test
    void testFindById() {
        Review foundReview = repository.findById(review.getReviewId());
        assertNotNull(foundReview);
        assertEquals(review.getReviewId(), foundReview.getReviewId());
        assertEquals(review.getReviewText(), foundReview.getReviewText());
        assertEquals(material.getMaterialId(), foundReview.getStudyMaterial().getMaterialId());
        assertEquals(user.getUserId(), foundReview.getUser().getUserId());
    }

    @Test
    void testFindByStudyMaterial() {
        Review review1 = new Review("Great!!!", material, user);
        Review review2 = new Review("Good!!!", material, user);
        repository.save(review1);
        repository.save(review2);

        List<Review> reviews = repository.findByStudyMaterial(material);
        assertNotNull(reviews);
        assertEquals(3, reviews.size(), "There should be 3 reviews in total");

        for (Review r : reviews) {
            assertEquals(material.getMaterialId(), r.getStudyMaterial().getMaterialId());
        }
    }



    @Test
    void testFindByUserAndMaterial() {
        // Test finding reviews by user and material
        List<Review> reviews = repository.findByUserAndMaterial(user, material);

        // Verify results
        assertNotNull(reviews);
        assertEquals(1, reviews.size(), "Should find one review");
        assertEquals(review.getReviewId(), reviews.get(0).getReviewId());
        assertEquals("Great!!!", reviews.get(0).getReviewText());
    }

    @Test
    void testHasUserReviewedMaterial_WhenReviewExists() {
        // Test when the user has reviewed the material
        boolean hasReviewed = repository.hasUserReviewedMaterial(user, material);

        // Verify result
        assertTrue(hasReviewed, "User should have reviewed the material");
    }

    @Test
    void testHasUserReviewedMaterial_WhenNoReviewExists() {
        // Create a new user who hasn't reviewed anything
        User anotherUser = new User("Another", "User", "another" + System.currentTimeMillis() + "@example.com", "password", user.getRole());
        anotherUser = userRepo.save(anotherUser);

        // Test when the user has not reviewed the material
        boolean hasReviewed = repository.hasUserReviewedMaterial(anotherUser, material);

        // Verify result
        assertFalse(hasReviewed, "New user should not have reviewed the material");
    }

    @Test
    void testDeleteById() {
        // Verify review exists before deletion
        Review foundReview = repository.findById(review.getReviewId());
        assertNotNull(foundReview, "Review should exist before deletion");

        // Delete the review
        repository.deleteById(review.getReviewId());

        // Verify review was deleted
        Review deletedReview = repository.findById(review.getReviewId());
        assertNull(deletedReview, "Review should be deleted");
    }

    @Test
    void testDeleteByMaterial_WithMultipleReviews() {
        // Create another user and review for the same material
        User anotherUser = new User("Second", "Reviewer", "second" + System.currentTimeMillis() + "@example.com", "password", user.getRole());
        anotherUser = userRepo.save(anotherUser);

        Review secondReview = new Review("Also good!", material, anotherUser);
        secondReview = repository.save(secondReview);

        // Verify we have multiple reviews for this material
        List<Review> initialReviews = repository.findByStudyMaterial(material);
        assertEquals(2, initialReviews.size(), "Should have 2 reviews for the material");

        // Delete all reviews for the material
        repository.deleteByMaterial(material);

        // Verify all reviews for the material are deleted
        List<Review> remainingReviews = repository.findByStudyMaterial(material);
        assertTrue(remainingReviews.isEmpty(), "All reviews for the material should be deleted");
    }

    @Test
    void testDeleteByMaterial_WithNoReviews() {
        // Create a material with no reviews
        StudyMaterial emptyMaterial = new StudyMaterial(
                user,
                "Empty Material",
                "This material has no reviews",
                "materials/empty.pdf",
                3.0f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.APPROVED
        );
        emptyMaterial = materialRepo.save(emptyMaterial);

        // Verify there are no reviews for this material
        List<Review> initialReviews = repository.findByStudyMaterial(emptyMaterial);
        assertTrue(initialReviews.isEmpty(), "Should have no reviews initially");

        // Test deleteByMaterial with a material that has no reviews
        repository.deleteByMaterial(emptyMaterial);

        // This is mainly to ensure no exceptions are thrown
        // No further assertions needed as there were no reviews to delete
    }


    @AfterAll
    void tearDown() {
        repository = null;
        roleRepo = null;
        userRepo = null;
        categoryRepo = null;
        materialRepo = null;
        TestPersistenceUtil.closeEntityManagerFactory();
    }
}
