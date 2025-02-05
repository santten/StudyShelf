import domain.model.*;
import infrastructure.repository.*;
import presentation.StudyShelfApplication;

import java.util.HashSet;
import java.util.Set;


public class Launcher {
    public static void main(String[] args) {
        // Test Hibernate user
        UserRepository userRepo = new UserRepository();
        User testUser = new User("Armas", "Nevolainen", "armas@gmail.com", "password", Role.STUDENT);
        User savedUser = userRepo.save(testUser);
        System.out.println("Saved user with ID: " + savedUser.getUserId());
        // Test Hibernate category
        CategoryRepository categoryRepo = new CategoryRepository();
        Category testCategory = new Category("Java", savedUser);
        Category savedCategory = categoryRepo.save(testCategory);
        System.out.println("Saved category with ID: " + savedCategory.getCategoryId());
        // Test Hibernate tag
        TagRepository tagRepo = new TagRepository();
        Tag testTag = new Tag("Java", savedUser);
        Tag savedTag = tagRepo.save(testTag);
        System.out.println("Saved tag with ID: " + savedTag.getTagId());
        Set<Tag> materialTags = new HashSet<>();
        materialTags.add(savedTag);
        //Test Hibernate study material
        StudyMaterial testMaterial = new StudyMaterial(
                savedUser,
                "Java for dummies",
                "Introduction to Java Programming for dummies",
                "materials/java-dumb.pdf",
                1.5f,
                "PDF",
                MaterialStatus.PENDING
        );
        testMaterial.setCategory(savedCategory);
        testMaterial.setTags(materialTags);
        StudyMaterialRepository materialRepo = new StudyMaterialRepository();
        StudyMaterial savedMaterial = materialRepo.save(testMaterial);
        System.out.println("Saved material with ID: " + savedMaterial.getMaterialId());
        // Test Hibernate rating
        RatingRepository ratingRepo = new RatingRepository();
        Rating testRating = new Rating(5, savedMaterial, savedUser);
        Rating savedRating = ratingRepo.save(testRating);
        System.out.println("Saved rating with ID: " + savedRating.getRatingId());

// Test Hibernate review
        ReviewRepository reviewRepo = new ReviewRepository();
        Review testReview = new Review("Great material for dummy like me!", savedMaterial, savedUser);
        Review savedReview = reviewRepo.save(testReview);
        System.out.println("Saved review with ID: " + savedReview.getReviewId());





        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}