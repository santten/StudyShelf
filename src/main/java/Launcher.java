import domain.service.RatingService;
import presentation.view.StudyShelfApplication;

import domain.model.*;
import infrastructure.repository.*;
import infrastructure.config.DatabaseInitializer;



public class Launcher {

    private static void testRatings() {
        UserRepository userRepo = new UserRepository();
        StudyMaterialRepository materialRepo = new StudyMaterialRepository();
        RatingRepository ratingRepo = new RatingRepository();
        RatingService ratingService = new RatingService(ratingRepo);

// Find own created user from database

        User testUser = userRepo.findByEmail("a@a");
        StudyMaterial material = materialRepo.findAllStudyMaterials().get(0);

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
        //DatabaseInitializer dbInit = new DatabaseInitializer();
        //dbInit.initializeRolesAndPermissions();
        //testRatings();

        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}