import domain.model.*;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;
import infrastructure.repository.UserRepository;
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





        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}