package domain.service;

import domain.model.*;
import infrastructure.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {
    private ReviewService reviewService;
    private ReviewRepository reviewRepository;

    private User testUser;
    private StudyMaterial testMaterial;

    @BeforeEach
    void setUp() {
        reviewRepository = Mockito.mock(ReviewRepository.class);
        reviewService = new ReviewService(reviewRepository);

        Role studentRole = new Role(RoleType.STUDENT);
        testUser = new User("Bob", "Johnson", "bob@example.com", "securePass", studentRole);

        testMaterial = new StudyMaterial(
                testUser,
                "Python Basics",
                "Introduction to Python",
                "materials/python.pdf",
                1.8f,
                "PDF",
                java.time.LocalDateTime.now(),
                MaterialStatus.PENDING
        );
    }

    @Test
    void testAddReview() {
        Review newReview = new Review("Great resource!", testMaterial, testUser);
        when(reviewRepository.save(any(Review.class))).thenReturn(newReview);

        Review savedReview = reviewService.addReview(testUser, testMaterial, "Great resource!");

        assertNotNull(savedReview);
        assertEquals("Great resource!", savedReview.getReviewText());
        assertEquals(testMaterial, savedReview.getStudyMaterial());
        assertEquals(testUser, savedReview.getUser());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testGetReviewsForMaterial() {
        Review review1 = new Review("Awesome!", testMaterial, testUser);
        Review review2 = new Review("Helpful!", testMaterial, testUser);

        when(reviewRepository.findByStudyMaterial(testMaterial)).thenReturn(List.of(review1, review2));

        List<Review> reviews = reviewService.getReviewsForMaterial(testMaterial);

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals("Awesome!", reviews.get(0).getReviewText());
        assertEquals("Helpful!", reviews.get(1).getReviewText());

        verify(reviewRepository, times(1)).findByStudyMaterial(testMaterial);
    }
}
