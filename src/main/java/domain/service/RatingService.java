package domain.service;

import domain.model.PermissionType;
import domain.model.Rating;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.RatingRepository;

import java.util.List;

/**
 * Service class responsible for handling rating-related operations,
 * such as creating, updating, deleting, and retrieving ratings on study materials.
 */
public class RatingService {
    private final RatingRepository ratingRepository;
    private final PermissionService permissionService;

    public RatingService(RatingRepository ratingRepository, PermissionService permissionService) {
        this.ratingRepository = ratingRepository;
        this.permissionService = permissionService;
    }

    /**
     * Creates a new rating for a study material by the user.
     * @throws SecurityException if the user lacks permission
     */
    // CREATE_RATING
    public Rating rateMaterial(int ratingScore, StudyMaterial material, User user) {
        if (!permissionService.hasPermission(user, PermissionType.CREATE_RATING)) {
            throw new SecurityException("You do not have permission to create a rating.");
        }
        Rating rating = new Rating(ratingScore, material, user);
        return ratingRepository.save(rating);
    }

    /**
     * Calculates and returns the average rating score for a material.
     */
    public double getAverageRating(StudyMaterial material) {
        return ratingRepository.findByMaterial(material)
                .stream()
                .mapToInt(Rating::getRatingScore)
                .average()
                .orElse(0.0);
    }

    /**
     * Updates the user's own rating with a new score.
     * @throws SecurityException if the user does not own the rating or lacks permission
     */
    // UPDATE_OWN_RATING
    public Rating updateRating(User user, Rating rating, int newScore) {
        if (!rating.getUser().equals(user)) {
            throw new SecurityException("You can only update your own rating.");
        }
        if (!permissionService.hasPermission(user, PermissionType.UPDATE_OWN_RATING)) {
            throw new SecurityException("You do not have permission to update your rating.");
        }
        rating.setRatingScore(newScore);
        return ratingRepository.save(rating);
    }

    /**
     * Retrieves all ratings for a given material.
     * @throws SecurityException if the user lacks permission
     */
    // READ_RATINGS
    public List<Rating> getRatings(User user, StudyMaterial material) {
        if (!permissionService.hasPermission(user, PermissionType.READ_RATINGS)) {
            throw new SecurityException("You do not have permission to read ratings.");
        }
        return ratingRepository.findByMaterial(material);
    }

    /**
     * Deletes a rating either by the owner or an admin.
     * @throws SecurityException if the user lacks permission
     */
    public void deleteRating(User user, Rating rating) {
        // DELETE_OWN_RATING
        boolean isOwner = rating.getUser().getUserId() == user.getUserId();
        boolean canDeleteOwn = isOwner && permissionService.hasPermission(user, PermissionType.DELETE_OWN_RATING);
        // DELETE_ANY_RATING
        boolean canDeleteAny = permissionService.hasPermission(user, PermissionType.DELETE_ANY_RATING);

        if (!canDeleteOwn && !canDeleteAny) {
            throw new SecurityException("You do not have permission to delete this rating.");
        }
        ratingRepository.deleteById(rating.getRatingId());
    }

    /**
     * Checks whether the user has already rated a material.
     * Users cannot rate their own uploads.
     */
    public boolean hasUserRatedMaterial(User user, StudyMaterial sm){
        if (sm.getUploader().getUserId() == user.getUserId()){
            return false;
        } else {
            return ratingRepository.hasUserReviewedMaterial(user, sm);
        }
    }

    /**
     * Retrieves all ratings made by a specific user.
     */
    public List<Rating> getRatingsByUser(User user) {
        return ratingRepository.findByUser(user);
    }

    /**
     * Retrieves rating(s) for a specific user and material.
     */
    public List<Rating> findRatingByUserAndMaterial(User user, StudyMaterial sm) {
        return ratingRepository.findByUserAndMaterial(user, sm);
    }
}
