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
    public static void main(String[] args) {

//        // Test Hibernate user
//        UserRepository userRepo = new UserRepository();
//        User testUser = new User("Armas", "Nevolainen", "armas@gmail.com", "password");
//        User savedUser = userRepo.save(testUser);
//        System.out.println("Saved user with ID: " + savedUser.getUserId());
//        // Test Hibernate role
//        RoleRepository roleRepo = new RoleRepository();
//        Role testRole = new Role("TEACHER");
//        Role savedRole = roleRepo.save(testRole);
//        System.out.println("Saved role with ID: " + savedRole.getId());
//
//        // Test Hibernate category
//        CategoryRepository categoryRepo = new CategoryRepository();
//        Category testCategory = new Category("Java", savedUser);
//        Category savedCategory = categoryRepo.save(testCategory);
//        System.out.println("Saved category with ID: " + savedCategory.getCategoryId());
//        // Test Hibernate tag
//        TagRepository tagRepo = new TagRepository();
//        Tag testTag = new Tag("Java", savedUser);
//        Tag savedTag = tagRepo.save(testTag);
//        System.out.println("Saved tag with ID: " + savedTag.getTagId());
//        Set<Tag> materialTags = new HashSet<>();
//        materialTags.add(savedTag);
//        //Test Hibernate study material
//        StudyMaterial testMaterial = new StudyMaterial(
//                savedUser,
//                "Java for dummies",
//                "Introduction to Java Programming for dummies",
//                "materials/java-dumb.pdf",
//                1.5f,
//                "PDF",
//                LocalDateTime.now(),
//                MaterialStatus.PENDING
//        );
//        testMaterial.setCategory(savedCategory);
//        testMaterial.setTags(materialTags);
//        StudyMaterialRepository materialRepo = new StudyMaterialRepository();
//        StudyMaterial savedMaterial = materialRepo.save(testMaterial);
//        System.out.println("Saved material with ID: " + savedMaterial.getMaterialId());
//        // Test Hibernate rating
//        RatingRepository ratingRepo = new RatingRepository();
//        Rating testRating = new Rating(5, savedMaterial, savedUser);
//        Rating savedRating = ratingRepo.save(testRating);
//        System.out.println("Saved rating with ID: " + savedRating.getRatingId());
//
//// Test Hibernate review
//        ReviewRepository reviewRepo = new ReviewRepository();
//        Review testReview = new Review("Great material for dummy like me!", savedMaterial, savedUser);
//        Review savedReview = reviewRepo.save(testReview);
//        System.out.println("Saved review with ID: " + savedReview.getReviewId());
//
//// Test Google Drive Upload
//        GoogleDriveService driveService = new GoogleDriveService();
//        StudyMaterialService materialService = new StudyMaterialService(driveService, materialRepo);
//
//        try {
//            byte[] content = Files.readAllBytes(Path.of("C:\\Users\\armas\\Downloads\\resort_hotel.txt")); // Replace with your file path
//
//            StudyMaterial uploadedMaterial = materialService.uploadMaterial(
//                    content,
//                    "resorthotel.txt",
//                    savedUser,
//                    "Resort Hotel Data",
//                    "Hotel information document"
//            );
//
//            System.out.println("Successfully uploaded file. URL: " + uploadedMaterial.getLink());
//
//        } catch (IOException e) {
//            System.out.println("Upload test failed: " + e.getMessage());
//            e.printStackTrace();
//        }

        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}