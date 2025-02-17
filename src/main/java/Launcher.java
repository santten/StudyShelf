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


        String[] defaultRoles = {"Student", "Teacher"};
        for (String roleName : defaultRoles) {
            Role role = roleRepo.findByName(roleName);
            if (role == null) {
                role = new Role(roleName);
                roleRepo.save(role);
                System.out.println("Created role: " + roleName);
            }
        }
    }
    private static void initializeTestMaterials() {
        UserRepository userRepo = new UserRepository();
        CategoryRepository categoryRepo = new CategoryRepository();
        StudyMaterialRepository materialRepo = new StudyMaterialRepository();

        User testUser = userRepo.findByEmail("armas@gmail.com");
        if (testUser == null) {
            testUser = new User("Armas", "Nevolainen", "armas@gmail.com", "123");
            testUser = userRepo.save(testUser);
        }

        Category javaCategory = new Category(0, "React", testUser);
        Category pythonCategory = new Category(0, "Python", testUser);
        javaCategory = categoryRepo.save(javaCategory);
        pythonCategory = categoryRepo.save(pythonCategory);

        for (int i = 1; i <= 5; i++) {
            StudyMaterial javaMaterial = new StudyMaterial(
                    testUser,
                    "Java for Dummies " + i,
                    "Introduction to Java Programming for Dummies Part " + i,
                    "materials/java-dumb-" + i + ".pdf",
                    1.5f,
                    "PDF",
                    LocalDateTime.now(),
                    MaterialStatus.PENDING
            );
            javaMaterial.setCategory(javaCategory);
            materialRepo.save(javaMaterial);
        }

        for (int i = 1; i <= 5; i++) {
            StudyMaterial pythonMaterial = new StudyMaterial(
                    testUser,
                    "Python for Dummies " + i,
                    "Introduction to Python Programming for Dummies Part " + i,
                    "materials/python-dumb-" + i + ".pdf",
                    1.2f,
                    "PDF",
                    LocalDateTime.now(),
                    MaterialStatus.PENDING
            );
            pythonMaterial.setCategory(pythonCategory);
            materialRepo.save(pythonMaterial);
        }
    }
    public static void main(String[] args) {
        // initializeRoles();
//         initializeTestMaterials();
        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}