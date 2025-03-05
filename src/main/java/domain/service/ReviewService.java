package domain.service;

import domain.model.PermissionType;
import domain.model.Review;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.ReviewRepository;

import java.util.List;

public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PermissionService permissionService;


    public ReviewService(ReviewRepository reviewRepository, PermissionService permissionService) {
        this.reviewRepository = reviewRepository;
        this.permissionService = permissionService;
    }

    // CREATE_REVIEW
    public Review addReview(User user, StudyMaterial material, String text) {
        if (user == null || material == null || text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: user, material, and text must not be null or empty");
        }
        if (!permissionService.hasPermission(user, PermissionType.CREATE_REVIEW)) {
            throw new SecurityException("You do not have permission to create a review.");
        }

        Review review = new Review(text, material, user);
        return reviewRepository.save(review);
    }

    // UPDATE_OWN_REVIEW
    public Review updateReview(User user, Review review, String newText) {
        if (review == null || newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input.");
        }
        if (!review.getUser().equals(user)) {
            throw new SecurityException("You can only update your own review.");
        }
        if (!permissionService.hasPermission(user, PermissionType.UPDATE_OWN_REVIEW)) {
            throw new SecurityException("You do not have permission to update your review.");
        }
        review.setReviewText(newText);
        return reviewRepository.save(review);
    }

    // READ_REVIEWS
    public List<Review> getReviewsForMaterial(User user, StudyMaterial material) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        if (!permissionService.hasPermission(user, PermissionType.READ_REVIEWS)) {
            throw new SecurityException("You do not have permission to read reviews.");
        }
        return reviewRepository.findByStudyMaterial(material);
    }

    public List<Review> getReviewsForMaterial(StudyMaterial material) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        return reviewRepository.findByStudyMaterial(material);
    }

    public void deleteReview(User user, Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null.");
        }
        // DELETE_OWN_REVIEW
        boolean isOwner = review.getUser().equals(user);
        boolean canDeleteOwn = isOwner && permissionService.hasPermission(user, PermissionType.DELETE_OWN_REVIEW);
       // DELETE_ANY_REVIEW
        boolean canDeleteAny = permissionService.hasPermission(user, PermissionType.DELETE_ANY_REVIEW);

        if (!(canDeleteOwn || canDeleteAny)) {
            throw new SecurityException("You do not have permission to delete this review.");
        }
        reviewRepository.delete(review);
    }

    public boolean hasUserReviewedMaterial(User user, StudyMaterial sm){
        if (sm.getUploader().getUserId() == user.getUserId()){
            return false;
        } else {
            return reviewRepository.hasUserReviewedMaterial(user, sm);
        }
    }
}


