package domain.service;

import domain.model.*;
import infrastructure.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {
    private ReviewService reviewService;
    private ReviewRepository reviewRepository;
    private PermissionService permissionService;

    private User testUser;
    private User adminUser;
    private StudyMaterial testMaterial;
    private Review testReview;

    @BeforeEach
    void setUp() {
        reviewRepository = Mockito.mock(ReviewRepository.class);
        permissionService = Mockito.mock(PermissionService.class);
        reviewService = new ReviewService(reviewRepository, permissionService);

        Role studentRole = new Role(RoleType.STUDENT);
        Role adminRole = new Role(RoleType.ADMIN);

        testUser = new User(1, "Bob", "Johnson", "bob@example.com", "securePass", studentRole);
        adminUser = new User(2, "Alice", "Smith", "alice@example.com", "securePass", adminRole);

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

        testReview = new Review("Great resource!", testMaterial, testUser);
        testReview.setReviewId(12341);
    }

    @Test
    void testAddReview_WithPermission() {
        when(permissionService.hasPermission(testUser, PermissionType.CREATE_REVIEW)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review savedReview = reviewService.addReview(testUser, testMaterial, "Great resource!");

        assertNotNull(savedReview);
        assertEquals("Great resource!", savedReview.getReviewText());
        assertEquals(testMaterial, savedReview.getStudyMaterial());
        assertEquals(testUser, savedReview.getUser());

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testAddReview_WithoutPermission() {
        when(permissionService.hasPermission(testUser, PermissionType.CREATE_REVIEW)).thenReturn(false);

        assertThrows(SecurityException.class, () ->
                reviewService.addReview(testUser, testMaterial, "Great resource!")
        );

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testUpdateReview_WithPermission() {
        when(permissionService.hasPermission(testUser, PermissionType.UPDATE_OWN_REVIEW)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review updatedReview = reviewService.updateReview(testUser, testReview, "Updated review!");

        assertNotNull(updatedReview);
        assertEquals("Updated review!", updatedReview.getReviewText());

        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void testUpdateReview_WithoutPermission() {
        when(permissionService.hasPermission(testUser, PermissionType.UPDATE_OWN_REVIEW)).thenReturn(false);

        assertThrows(SecurityException.class, () ->
                reviewService.updateReview(testUser, testReview, "Updated review!")
        );

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testDeleteReview_AsOwner_WithPermission() {
        when(permissionService.hasPermission(testUser, PermissionType.DELETE_OWN_REVIEW)).thenReturn(true);

        reviewService.deleteReview(testUser, testReview);

        verify(reviewRepository, times(1)).deleteById(testReview.getReviewId());
    }

    @Test
    void testDeleteReview_AsAdmin_WithPermission() {
        when(permissionService.hasPermission(adminUser, PermissionType.DELETE_ANY_REVIEW)).thenReturn(true);

        reviewService.deleteReview(adminUser, testReview);

        verify(reviewRepository, times(1)).deleteById(testReview.getReviewId());
    }

    @Test
    void testDeleteReview_WithoutPermission() {
        when(permissionService.hasPermission(testUser, PermissionType.DELETE_OWN_REVIEW)).thenReturn(false);
        when(permissionService.hasPermission(testUser, PermissionType.DELETE_ANY_REVIEW)).thenReturn(false);

        assertThrows(SecurityException.class, () -> reviewService.deleteReview(testUser, testReview));

        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void testGetReviewsForMaterial_WithPermission() {
        Review review1 = new Review("Awesome!", testMaterial, testUser);
        Review review2 = new Review("Helpful!", testMaterial, testUser);

        when(permissionService.hasPermission(testUser, PermissionType.READ_REVIEWS)).thenReturn(true);
        when(reviewRepository.findByStudyMaterial(testMaterial)).thenReturn(List.of(review1, review2));

        List<Review> reviews = reviewService.getReviewsForMaterial(testUser, testMaterial);

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals("Awesome!", reviews.get(0).getReviewText());
        assertEquals("Helpful!", reviews.get(1).getReviewText());

        verify(reviewRepository, times(1)).findByStudyMaterial(testMaterial);
    }

    @Test
    void testGetReviewsForMaterial_WithoutPermission() {
        when(permissionService.hasPermission(testUser, PermissionType.READ_REVIEWS)).thenReturn(false);

        assertThrows(SecurityException.class, () ->
                reviewService.getReviewsForMaterial(testUser, testMaterial)
        );

        verify(reviewRepository, never()).findByStudyMaterial(any(StudyMaterial.class));
    }

    @Test
    void findReviewByUserAndMaterial(){
        when(reviewRepository.findByUserAndMaterial(testUser, testMaterial)).thenReturn(List.of(testReview));

        List<Review> reviews = reviewService.findReviewByUserAndMaterial(testUser, testMaterial);

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals(testReview, reviews.get(0));

        verify(reviewRepository, times(1)).findByUserAndMaterial(testUser, testMaterial);
    }

    @Test
    void getReviewsForMaterial(){
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
