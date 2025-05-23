package presentation.controller;

import domain.model.*;
import domain.service.PermissionService;
import domain.service.RatingService;
import domain.service.ReviewService;
import infrastructure.repository.RatingRepository;
import infrastructure.repository.ReviewRepository;
import presentation.utility.CustomAlert;
import presentation.utility.GUILogger;
import presentation.view.CurrentUserManager;
import presentation.view.LanguageManager;

import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class RatingController {
    private final RatingService ratingService = new RatingService(new RatingRepository(), new PermissionService());

    public boolean deleteRatingAndReview(User user, StudyMaterial sm) {
        ReviewService reviewService = new ReviewService(new ReviewRepository(), new PermissionService());

        List<Rating> foundRating = ratingService.findRatingByUserAndMaterial(user, sm);
        List<Review> foundReview = reviewService.findReviewByUserAndMaterial(user, sm);

        User curUser = CurrentUserManager.get();
        boolean canDeleteOwn = curUser.hasPermission(PermissionType.DELETE_OWN_RATING);
        boolean canDeleteAny = curUser.hasPermission(PermissionType.DELETE_ANY_RATING);

        if (!canDeleteOwn && !canDeleteAny) {
            ResourceBundle rb = LanguageManager.getInstance().getBundle();
            CustomAlert.show(WARNING, rb.getString("alertPermissionDenied"), rb.getString("alertPermissionDeniedSub"));
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