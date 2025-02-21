import domain.service.RatingService;
import presentation.view.StudyShelfApplication;

import domain.model.*;
import domain.service.StudyMaterialService;
import infrastructure.repository.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import domain.service.GoogleDriveService;
import java.nio.file.Files;
import java.nio.file.Path;

public class Launcher {

    private static void initializeRoles() {
    RoleRepository roleRepo = new RoleRepository();
    RoleType[] defaultRoles = {RoleType.STUDENT, RoleType.TEACHER};
    for(RoleType roleType :defaultRoles)

    {
        Role role = roleRepo.findByName(roleType);
        if (role == null) {
            role = new Role(roleType);
            roleRepo.save(role);
        }
    }
}

    private static void testRatings() {
        UserRepository userRepo = new UserRepository();
        StudyMaterialRepository materialRepo = new StudyMaterialRepository();
        RatingRepository ratingRepo = new RatingRepository();
        RatingService ratingService = new RatingService(ratingRepo);

        User testUser = userRepo.findByEmail("a@a");
        StudyMaterial material = materialRepo.findAllStudyMaterials().get(1);

        Rating rating = ratingService.rateMaterial(5, material, testUser);
        System.out.println("Added rating: " + rating.getRatingScore());
        Rating rating1 = ratingService.rateMaterial(5, material, testUser);
        System.out.println("Added rating: " + rating1.getRatingScore());
        Rating rating2 = ratingService.rateMaterial(5, material, testUser);
        System.out.println("Added rating: " + rating2.getRatingScore());

        double avgRating = ratingService.getAverageRating(material);
        System.out.println("Average rating: " + avgRating);
    }
//    private static void initializeTestMaterials() {
//        UserRepository userRepo = new UserRepository();
//        CategoryRepository categoryRepo = new CategoryRepository();
//        StudyMaterialRepository materialRepo = new StudyMaterialRepository();
//
//        User testUser = userRepo.findByEmail("armas@gmail.com");
//        if (testUser == null) {
//            testUser = new User("Armas", "Nevolainen", "armas@gmail.com", "123");
//            testUser = userRepo.save(testUser);
//        }
//
//        Category javaCategory = new Category(0, "React", testUser);
//        Category pythonCategory = new Category(0, "Python", testUser);
//        javaCategory = categoryRepo.save(javaCategory);
//        pythonCategory = categoryRepo.save(pythonCategory);

//        for (int i = 1; i <= 5; i++) {
//            StudyMaterial javaMaterial = new StudyMaterial(
//                    testUser,
//                    "Java for Dummies " + i,
//                    "Introduction to Java Programming for Dummies Part " + i,
//                    "materials/java-dumb-" + i + ".pdf",
//                    1.5f,
//                    "PDF",
//                    LocalDateTime.now(),
//                    MaterialStatus.PENDING
//            );
//            javaMaterial.setCategory(javaCategory);
//            materialRepo.save(javaMaterial);
//        }
//
//        for (int i = 1; i <= 5; i++) {
//            StudyMaterial pythonMaterial = new StudyMaterial(
//                    testUser,
//                    "Python for Dummies " + i,
//                    "Introduction to Python Programming for Dummies Part " + i,
//                    "materials/python-dumb-" + i + ".pdf",
//                    1.2f,
//                    "PDF",
//                    LocalDateTime.now(),
//                    MaterialStatus.PENDING
//            );
//            pythonMaterial.setCategory(pythonCategory);
//            materialRepo.save(pythonMaterial);
//        }
//    }
    public static void main(String[] args) {
        initializeRoles();
       testRatings();
//         initializeTestMaterials();
        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}