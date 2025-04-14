package domain.service;

import domain.model.PermissionType;
import domain.model.Review;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.ReviewRepository;
import infrastructure.repository.ReviewTranslationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presentation.view.LanguageManager;

import java.util.List;
import java.util.Map;


/**
 * Service layer handling logic for creating, updating, retrieving,
 * deleting and translating reviews on study materials.
 */
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final PermissionService permissionService;
    private final ReviewTranslationRepository translationRepository;
    private final TranslationService translationService;

    public ReviewService(ReviewRepository reviewRepository, PermissionService permissionService) {
        this.reviewRepository = reviewRepository;
        this.permissionService = permissionService;
        this.translationRepository = new ReviewTranslationRepository();
        this.translationService = new TranslationService();
    }

    /**
     * Adds a new review and automatically translates it into supported languages.
     */
    // CREATE_REVIEW
    public Review addReview(User user, StudyMaterial material, String text) {
        if (user == null || material == null || text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: user, material, and text must not be null or empty");
        }
        if (!permissionService.hasPermission(user, PermissionType.CREATE_REVIEW)) {
            throw new SecurityException("You do not have permission to create a review.");
        }

        Review review = new Review(text, material, user);
        Review savedReview = reviewRepository.save(review);

        // Get current language from LanguageManager
        String sourceLanguage = LanguageManager.getInstance().getCurrentLanguage();

        try {

            // Translate the review text to all supported languages
            Map<String, String> translations = translationService.translateToAllLanguages(text, sourceLanguage);

            // Save translations
            translationRepository.saveTranslations(savedReview.getReviewId(), translations);
            logger.info("Review created with ID {} and translated to {} languages",
                    savedReview.getReviewId(), translations.size());
        } catch (Exception e) {
            logger.error("Failed to translate review", e);
        }

        return savedReview;
    }

    /**
     * Updates an existing review text and re-generates its translations.
     */
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
        Review updatedReview = reviewRepository.save(review);

        // Get current language from LanguageManager
        String sourceLanguage = LanguageManager.getInstance().getCurrentLanguage();

        try {
            // Translate the review text to all supported languages
            Map<String, String> translations = translationService.translateToAllLanguages(newText, sourceLanguage);

            // Save translations
            translationRepository.saveTranslations(updatedReview.getReviewId(), translations);
            logger.info("Review updated with ID {} and translated to {} languages",
                    updatedReview.getReviewId(), translations.size());
        } catch (Exception e) {
            logger.error("Failed to translate updated review", e);
            // Continue with the review update even if translation fails
        }

        return updatedReview;
    }

    /**
     * Retrieves all reviews for a given material if user has READ permission.
     */
    // READ_REVIEWS
    public List<Review> getReviewsForMaterial(User user, StudyMaterial material) {
        if (!permissionService.hasPermission(user, PermissionType.READ_REVIEWS)) {
            throw new SecurityException("You do not have permission to read reviews.");
        }
        return reviewRepository.findByStudyMaterial(material);
    }

    /**
     * Retrieves all reviews for a material without user validation.
     */
    public List<Review> getReviewsForMaterial(StudyMaterial material) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        return reviewRepository.findByStudyMaterial(material);
    }

    /**
     * Deletes a review. Only the owner or an admin can delete it.
     */
    public void deleteReview(User user, Review review) {
        // DELETE_OWN_REVIEW
        boolean isOwner = review.getUser().getUserId() == user.getUserId();
        boolean canDeleteOwn = isOwner && permissionService.hasPermission(user, PermissionType.DELETE_OWN_REVIEW);
        // DELETE_ANY_REVIEW
        boolean canDeleteAny = permissionService.hasPermission(user, PermissionType.DELETE_ANY_REVIEW);

        if (!(canDeleteOwn || canDeleteAny)) {
            throw new SecurityException("You do not have permission to delete this review.");
        }
        reviewRepository.deleteById(review.getReviewId());
    }

    /**
     * Finds review(s) written by a specific user for a material.
     */
    public List<Review> findReviewByUserAndMaterial(User user, StudyMaterial sm) {
        return reviewRepository.findByUserAndMaterial(user, sm);
    }

    /**
     * Returns the translated text of a review for the current language.
     */
    public String getTranslatedReviewText(Review review) {
        if (review == null) {
            return "";
        }

        String currentLanguage = LanguageManager.getInstance().getCurrentLanguage();
        String translatedText = translationRepository.getTranslation(review.getReviewId(), currentLanguage);

        // If no translation found, return original text
        return translatedText != null ? translatedText : review.getReviewText();
    }

    /**
     * Returns the original review text.
     */
    public String getOriginalReviewText(Review review) {
        if (review == null) {
            return "";
        }

        return review.getReviewText();
    }
}
