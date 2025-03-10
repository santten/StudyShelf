package domain.service;

import domain.model.PermissionType;
import domain.model.Rating;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.RatingRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

public class RatingService {
    private final RatingRepository ratingRepository;
    private final PermissionService permissionService;

    public RatingService(RatingRepository ratingRepository, PermissionService permissionService) {
        this.ratingRepository = ratingRepository;
        this.permissionService = permissionService;
    }

    // CREATE_RATING
    public Rating rateMaterial(int ratingScore, StudyMaterial material, User user) {
        if (!permissionService.hasPermission(user, PermissionType.CREATE_RATING)) {
            throw new SecurityException("You do not have permission to create a rating.");
        }
        Rating rating = new Rating(ratingScore, material, user);
        return ratingRepository.save(rating);
    }

    public double getAverageRating(StudyMaterial material) {
        return ratingRepository.findByMaterial(material)
                .stream()
                .mapToInt(Rating::getRatingScore)
                .average()
                .orElse(0.0);
    }

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

    // READ_RATINGS
    public List<Rating> getRatings(User user, StudyMaterial material) {
        if (!permissionService.hasPermission(user, PermissionType.READ_RATINGS)) {
            throw new SecurityException("You do not have permission to read ratings.");
        }
        return ratingRepository.findByMaterial(material);
    }

    public void deleteRating(User user, Rating rating) {
        // DELETE_OWN_RATING
        boolean isOwner = rating.getUser().equals(user);
        boolean canDeleteOwn = isOwner && permissionService.hasPermission(user, PermissionType.DELETE_OWN_RATING);
        // DELETE_ANY_RATING
        boolean canDeleteAny = permissionService.hasPermission(user, PermissionType.DELETE_ANY_RATING);

        if (!(canDeleteOwn || canDeleteAny)) {
            throw new SecurityException("You do not have permission to delete this rating.");
        }
        ratingRepository.deleteById(rating.getRatingId());
    }

    public boolean hasUserRatedMaterial(User user, StudyMaterial sm){
        if (sm.getUploader().getUserId() == user.getUserId()){
            return false;
        } else {
            return ratingRepository.hasUserReviewedMaterial(user, sm);
        }
    }

    public List<Rating> getRatingsByUser(User user) {
        return ratingRepository.findByUser(user);
    }

    public List<Rating> findRatingByUserAndMaterial(User user, StudyMaterial sm) {
        return ratingRepository.findByUserAndMaterial(user, sm);
    }
}
