package presentation.controller;

import domain.model.PermissionType;
import domain.model.Review;
import domain.model.StudyMaterial;
import domain.model.User;
import domain.service.PermissionService;
import domain.service.ReviewService;
import domain.service.Session;
import infrastructure.repository.ReviewRepository;
import javafx.scene.control.Alert;

import java.util.List;

public class ReviewController extends BaseController {
    private final ReviewService reviewService = new ReviewService(new ReviewRepository(), new PermissionService());

    public List<Review> getReviews(StudyMaterial material) {
        return reviewService.getReviewsForMaterial(material);
    }

    public void addReview(StudyMaterial material, String text) {
        User user = Session.getInstance().getCurrentUser();
        if (user == null || !hasPermission(PermissionType.CREATE_REVIEW)) {
            showAlert("Permission Denied", "You do not have permission to add reviews.");
            return;
        }
        reviewService.addReview(user, material, text);
    }

    public void deleteReview(Review review) {
        User user = Session.getInstance().getCurrentUser();
        if (user == null) {
            showAlert("Permission Denied", "You are not logged in.");
            return;
        }

        boolean canDeleteOwn = review.getUser().equals(user) && hasPermission(PermissionType.DELETE_OWN_REVIEW);
        boolean canDeleteAny = hasPermission(PermissionType.DELETE_ANY_REVIEW);

        if (canDeleteOwn || canDeleteAny) {
            reviewService.deleteReview(user, review);
        } else {
            showAlert("Permission Denied", "You do not have permission to delete this review.");
        }
    }

    public void updateReview(Review review, String newText) {
        User user = Session.getInstance().getCurrentUser();
        if (user == null || !hasPermission(PermissionType.UPDATE_OWN_REVIEW)) {
            showAlert("Permission Denied", "You do not have permission to update reviews.");
            return;
        }
        reviewService.updateReview(user, review, newText);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
