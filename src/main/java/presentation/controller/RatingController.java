package presentation.controller;

import domain.model.PermissionType;
import domain.model.Rating;
import domain.model.StudyMaterial;
import domain.model.User;
import domain.service.PermissionService;
import domain.service.RatingService;
import domain.service.Session;
import infrastructure.repository.RatingRepository;
import javafx.scene.control.Alert;

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

    public void deleteRating(Rating rating) {
        User user = Session.getInstance().getCurrentUser();
        if (user == null) {
            showAlert("Permission Denied", "You are not logged in.");
            return;
        }

        boolean canDeleteOwn = rating.getUser().equals(user) && hasPermission(PermissionType.DELETE_OWN_RATING);
        boolean canDeleteAny = hasPermission(PermissionType.DELETE_ANY_RATING);

        if (canDeleteOwn || canDeleteAny) {
            ratingService.deleteRating(user, rating);
        } else {
            showAlert("Permission Denied", "You do not have permission to delete this rating.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}