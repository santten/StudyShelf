import domain.service.PermissionService;
import domain.service.RatingService;
import presentation.view.StudyShelfApplication;

import domain.model.*;
import infrastructure.repository.*;
import infrastructure.config.DatabaseInitializer;

import java.util.List;


public class Launcher {

    private static void testRatings() {
        UserRepository userRepo = new UserRepository();
        StudyMaterialRepository materialRepo = new StudyMaterialRepository();
        PermissionService permissionService = new PermissionService();
        RatingRepository ratingRepo = new RatingRepository();
        RatingService ratingService = new RatingService(ratingRepo, permissionService);

// Find own created user from database

        User testUser = userRepo.findByEmail("a@a");
        List<StudyMaterial> materials = materialRepo.findAll();
        if (materials.isEmpty()) {
            System.out.println("No study materials found!");
            return;
        }
        StudyMaterial material = materials.get(0);

// User for now can create multiple ratings for same material

        Rating rating = ratingService.rateMaterial(5, material, testUser);
        System.out.println("Added rating: " + rating.getRatingScore());
        Rating rating1 = ratingService.rateMaterial(4, material, testUser);
        System.out.println("Added rating: " + rating1.getRatingScore());
        Rating rating2 = ratingService.rateMaterial(2, material, testUser);
        System.out.println("Added rating: " + rating2.getRatingScore());


        double avgRating = ratingService.getAverageRating(material);
        System.out.println("Average rating: " + avgRating);
    }


    public static void main(String[] args) {
        DatabaseInitializer dbInit = new DatabaseInitializer();
        dbInit.initializeRolesAndPermissions();
        // testRatings();

        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}