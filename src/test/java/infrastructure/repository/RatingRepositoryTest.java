package infrastructure.repository;

import domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class RatingRepositoryTest {
    private RatingRepository repository;
    private User user;
    private StudyMaterial material;
    private Rating testRating;

    @BeforeEach
    void setUp() {
        repository = new RatingRepository();

        UserRepository userRepo = new UserRepository();
        user = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password");
        user = userRepo.save(user);

        StudyMaterialRepository materialRepo = new StudyMaterialRepository();
        material = new StudyMaterial(user, "Java", "Best Practices",
                "link", 100.0f, "PDF", LocalDateTime.now(), MaterialStatus.APPROVED);
        material = materialRepo.save(material);

        // Create test rating
        testRating = new Rating(5, material, user);
    }

    @Test
    void save() {
        Rating savedRating = repository.save(testRating);
        assertNotNull(savedRating);
        assertNotNull(savedRating.getRatingId());
        assertEquals(5, savedRating.getRatingScore());
        assertEquals(material.getMaterialId(), savedRating.getStudyMaterial().getMaterialId());
        assertEquals(user.getUserId(), savedRating.getUser().getUserId());
    }

    @Test
    void findById() {
        Rating savedRating = repository.save(testRating);
        Rating foundRating = repository.findById(savedRating.getRatingId());
        assertNotNull(foundRating);
        assertEquals(savedRating.getRatingId(), foundRating.getRatingId());
        assertEquals(savedRating.getRatingScore(), foundRating.getRatingScore());
        assertEquals(material.getMaterialId(), foundRating.getStudyMaterial().getMaterialId());
        assertEquals(user.getUserId(), foundRating.getUser().getUserId());
    }
}
