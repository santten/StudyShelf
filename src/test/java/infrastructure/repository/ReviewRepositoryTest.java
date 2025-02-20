package infrastructure.repository;

import domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewRepositoryTest {
    private ReviewRepository repository;
    private User user;
    private StudyMaterial material;
    private Review review;
    private RoleRepository roleRepo;
    private UserRepository userRepo;
    private CategoryRepository categoryRepo;
    private StudyMaterialRepository materialRepo;

//    @BeforeEach
//    void setUp() {
//        repository = new ReviewRepository();
//        UserRepository userRepo = new UserRepository();
//        StudyMaterialRepository materialRepo = new StudyMaterialRepository();
//        Role testUser = new Role(RoleType.STUDENT);
//        user = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password",testUser);
//        user = userRepo.save(user);
//        CategoryRepository categoryRepo = new CategoryRepository();
//        Category category = new Category("Java", user);
//        category = categoryRepo.save(category);
//        material = new StudyMaterial(
//                user,
//                "Java for dummies",
//                "Introduction to Java Programming for dummies",
//                "materials/java-dumb.pdf",
//                10f,
//                "PDF",
//                LocalDateTime.now(),
//                MaterialStatus.PENDING
//        );
//        material.setCategory(category);
//        material = materialRepo.save(material);
//        review = new Review("Great!!!", material, user);
//    }
    @BeforeEach
    void setUp() {
        repository = new ReviewRepository();
        roleRepo = new RoleRepository();
        userRepo = new UserRepository();
        categoryRepo = new CategoryRepository();
        materialRepo = new StudyMaterialRepository();

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
        material.setCategory(category); // **先设置 Category**
        material = materialRepo.save(material);

        review = new Review("Great!!!", material, user);
    }

    @Test
    void save() {
        Review savedReview = repository.save(review);
        assertNotNull(savedReview);
        assertNotNull(savedReview.getReviewId());
        assertEquals("Great!!!", savedReview.getReviewText());
        assertEquals(material.getMaterialId(), savedReview.getStudyMaterial().getMaterialId());
        assertEquals(user.getUserId(), savedReview.getUser().getUserId());
    }

    @Test
    void findById() {
        Review savedReview = repository.save(review);
        Review foundReview = repository.findById(savedReview.getReviewId());
        assertNotNull(foundReview);
        assertEquals(savedReview.getReviewId(), foundReview.getReviewId());
        assertEquals(savedReview.getReviewText(), foundReview.getReviewText());
        assertEquals(material.getMaterialId(), foundReview.getStudyMaterial().getMaterialId());
        assertEquals(user.getUserId(), foundReview.getUser().getUserId());
    }
}