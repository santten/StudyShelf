import domain.model.StudyMaterial;
import domain.model.User;
import domain.model.Role;
import domain.model.MaterialStatus;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.UserRepository;
import presentation.StudyShelfApplication;


public class Launcher {
    public static void main(String[] args) {
        // Test Hibernate user
        UserRepository userRepo = new UserRepository();
        User testUser = new User("Armas", "Nevolainen", "armas@gmail.com", "password", Role.STUDENT);
        User savedUser = userRepo.save(testUser);
        System.out.println("Saved user with ID: " + savedUser.getUserId());
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

        StudyMaterialRepository materialRepo = new StudyMaterialRepository();
        StudyMaterial savedMaterial = materialRepo.save(testMaterial);
        System.out.println("Saved material with ID: " + savedMaterial.getMaterialId());



        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}