package domain.service;

import domain.model.PermissionType;
import domain.model.Rating;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceTest {
    private RatingService ratingService;
    private RatingRepository ratingRepository;
    private PermissionService permissionService;
    private User user;
    private StudyMaterial material;

    @BeforeEach
    void setUp() {
        ratingRepository = Mockito.mock(RatingRepository.class);
        permissionService = Mockito.mock(PermissionService.class);
        ratingService = new RatingService(ratingRepository, permissionService);

        user = new User();
        material = new StudyMaterial();
    }

    @Test
    void testRateMaterial_Success() {
        when(permissionService.hasPermission(user, PermissionType.CREATE_RATING)).thenReturn(true);
        Rating rating = new Rating(5, material, user);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        Rating createdRating = ratingService.rateMaterial(5, material, user);

        assertNotNull(createdRating);
        assertEquals(5, createdRating.getRatingScore());
        assertEquals(material, createdRating.getStudyMaterial());
        assertEquals(user, createdRating.getUser());

        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void testRateMaterial_NoPermission() {
        when(permissionService.hasPermission(user, PermissionType.CREATE_RATING)).thenReturn(false);

        Exception exception = assertThrows(SecurityException.class, () ->
                ratingService.rateMaterial(4, material, user));

        assertEquals("You do not have permission to create a rating.", exception.getMessage());
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void testGetAverageRating() {
        List<Rating> ratings = Arrays.asList(
                new Rating(4, material, user),
                new Rating(5, material, user),
                new Rating(3, material, user)
        );

        when(ratingRepository.findByMaterial(material)).thenReturn(ratings);

        double avg = ratingService.getAverageRating(material);
        assertEquals(4.0, avg, 0.01);
    }

    @Test
    void testGetAverageRating_NoRatings() {
        when(ratingRepository.findByMaterial(material)).thenReturn(List.of());

        double avg = ratingService.getAverageRating(material);
        assertEquals(0.0, avg);
    }

    @Test
    void testUpdateRating_Success() {
        Rating rating = new Rating(3, material, user);
        when(permissionService.hasPermission(user, PermissionType.UPDATE_OWN_RATING)).thenReturn(true);
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rating updatedRating = ratingService.updateRating(user, rating, 5);

        assertEquals(5, updatedRating.getRatingScore());
        verify(ratingRepository, times(1)).save(rating);
    }

    @Test
    void testUpdateRating_NoPermission() {
        Rating rating = new Rating(3, material, user);
        when(permissionService.hasPermission(user, PermissionType.UPDATE_OWN_RATING)).thenReturn(false);

        Exception exception = assertThrows(SecurityException.class, () ->
                ratingService.updateRating(user, rating, 5));

        assertEquals("You do not have permission to update your rating.", exception.getMessage());
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void testUpdateRating_NotOwner() {
        User anotherUser = new User();
        Rating rating = new Rating(3, material, anotherUser);

        Exception exception = assertThrows(SecurityException.class, () ->
                ratingService.updateRating(user, rating, 5));

        assertEquals("You can only update your own rating.", exception.getMessage());
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void testGetRatings_Success() {
        when(permissionService.hasPermission(user, PermissionType.READ_RATINGS)).thenReturn(true);
        List<Rating> ratings = Arrays.asList(
                new Rating(5, material, user),
                new Rating(4, material, user)
        );
        when(ratingRepository.findByMaterial(material)).thenReturn(ratings);

        List<Rating> result = ratingService.getRatings(user, material);
        assertEquals(2, result.size());
    }

    @Test
    void testGetRatings_NoPermission() {
        when(permissionService.hasPermission(user, PermissionType.READ_RATINGS)).thenReturn(false);

        Exception exception = assertThrows(SecurityException.class, () ->
                ratingService.getRatings(user, material));

        assertEquals("You do not have permission to read ratings.", exception.getMessage());
        verify(ratingRepository, never()).findByMaterial(any(StudyMaterial.class));
    }

    @Test
    void testDeleteRating_OwnerSuccess() {
        Rating rating = new Rating(4, material, user);
        when(permissionService.hasPermission(user, PermissionType.DELETE_OWN_RATING)).thenReturn(true);
        rating.setRatingId(15125);
        ratingService.deleteRating(user, rating);

        verify(ratingRepository, times(1)).deleteById(rating.getRatingId());
    }

    @Test
    void testDeleteRating_AdminSuccess() {
        User admin = new User();
        Rating rating = new Rating(4, material, user);
        rating.setRatingId(21341);
        when(permissionService.hasPermission(admin, PermissionType.DELETE_ANY_RATING)).thenReturn(true);

        ratingService.deleteRating(admin, rating);

        verify(ratingRepository, times(1)).deleteById(rating.getRatingId());
    }

    @Test
    void testDeleteRating_NoPermission() {
        Rating rating = new Rating(4, material, user);
        when(permissionService.hasPermission(user, PermissionType.DELETE_OWN_RATING)).thenReturn(false);
        when(permissionService.hasPermission(user, PermissionType.DELETE_ANY_RATING)).thenReturn(false);

        Exception exception = assertThrows(SecurityException.class, () ->
                ratingService.deleteRating(user, rating));

        assertEquals("You do not have permission to delete this rating.", exception.getMessage());
        verify(ratingRepository, never()).delete(any(Rating.class));
    }
}
