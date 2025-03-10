package presentation.controller;

import domain.model.*;
import domain.service.PermissionService;
import domain.service.RatingService;
import domain.service.ReviewService;
import domain.service.Session;
import infrastructure.repository.RatingRepository;
import infrastructure.repository.ReviewRepository;
import javafx.scene.control.Alert;
import presentation.utility.GUILogger;

import java.util.List;

public class RatingController extends BaseController {
    private final RatingService ratingService = new RatingService(new RatingRepository(), new PermissionService());

    public void addRating(StudyMaterial material, int ratingScore) {
        User user = Session.getInstance().getCurrentUser();
        if (user == null || !hasPermission(PermissionType.CREATE_RATING)) {
            showAlert("Permission Denied", "You do not have permission to rate.");
            return;
        }
        ratingService.rateMaterial(ratingScore, material, user);
    }

    public double getAverageRating(StudyMaterial material) {
        return ratingService.getAverageRating(material);
    }

    public void updateRating(Rating rating, int newScore) {
        User user = Session.getInstance().getCurrentUser();
        if (user == null || !hasPermission(PermissionType.UPDATE_OWN_RATING)) {
            showAlert("Permission Denied", "You do not have permission to update ratings.");
            return;
        }
        ratingService.updateRating(user, rating, newScore);
    }

    public boolean deleteRating(Rating rating) {
        User user = Session.getInstance().getCurrentUser();
        if (user == null) {
            showAlert("Permission Denied", "You are not logged in.");
            return false;
        }

        boolean canDeleteOwn = rating.getUser().equals(user) && hasPermission(PermissionType.DELETE_OWN_RATING);
        boolean canDeleteAny = hasPermission(PermissionType.DELETE_ANY_RATING);

        if (canDeleteOwn || canDeleteAny) {
            ratingService.deleteRating(user, rating);
            return true;
        } else {
            showAlert("Permission Denied", "You do not have permission to delete this rating.");
        }
        return false;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean deleteRatingAndReview(User user, StudyMaterial sm) {
        ReviewService reviewService = new ReviewService(new ReviewRepository(), new PermissionService());

        List<Rating> foundRating = ratingService.findRatingByUserAndMaterial(user, sm);
        List<Review> foundReview = reviewService.findReviewByUserAndMaterial(user, sm);

        boolean canDeleteOwn = hasPermission(PermissionType.DELETE_OWN_RATING);
        boolean canDeleteAny = hasPermission(PermissionType.DELETE_ANY_RATING);

        if (!canDeleteOwn && !canDeleteAny) {
            showAlert("Permission Denied", "You do not have permission to delete this rating.");
            return false;
        }

        if (!foundRating.isEmpty()){
            foundRating.forEach(rating -> ratingService.deleteRating(user, rating));
            if (foundReview.isEmpty()) {
                GUILogger.info("User has not reviewed this material, but has rated it");
            } else {
                foundReview.forEach(review -> reviewService.deleteReview(user, review));
            }
            return true;
        } else {
            GUILogger.info("User has not rated this material");
            return false;
        }
    }
}