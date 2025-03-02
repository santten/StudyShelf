package infrastructure.repository;

import domain.model.*;
import org.junit.jupiter.api.*;

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

    @BeforeAll
    void setupDatabase() {
        repository = new ReviewRepository();
        roleRepo = new RoleRepository();
        userRepo = new UserRepository();
        categoryRepo = new CategoryRepository();
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

    @AfterAll
    void tearDown() {
        repository = null;
        roleRepo = null;
        userRepo = null;
        categoryRepo = null;
        materialRepo = null;
    }
}
