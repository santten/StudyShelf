import domain.model.User;
import domain.model.Role;
import infrastructure.repository.UserRepository;
import presentation.StudyShelfApplication;


public class Launcher {
    public static void main(String[] args) {
        StudyShelfApplication.launch(StudyShelfApplication.class);
        // Test Hibernate
        UserRepository userRepo = new UserRepository();
        User testUser = new User("Armas", "Nevolainen", "armas@gmail.com", "password", Role.STUDENT);
        User savedUser = userRepo.save(testUser);
        System.out.println("Saved user with ID: " + savedUser.getUserId());
    }
}