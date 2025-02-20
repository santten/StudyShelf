package infrastructure.repository;

import domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class StudyMaterialRepositoryTest {
    private StudyMaterialRepository repository;
    private User user;
    private Category category;
    private StudyMaterial testMaterial;
    private RoleRepository roleRepo;
    private UserRepository userRepo;
    private CategoryRepository categoryRepo;

//    @BeforeEach
//    void setUp() {
//        repository = new StudyMaterialRepository();
//        Role testRole = new Role(RoleType.STUDENT);
//        UserRepository userRepo = new UserRepository();
//        user = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password",testRole);
//        user = userRepo.save(user);
//        CategoryRepository categoryRepo = new CategoryRepository();
//        category = new Category("Test Category", user);
//        category = categoryRepo.save(category);
//
//        testMaterial = new StudyMaterial(
//                user,
//                "Java for dummies",
//                "Introduction to Java Programming for dummies",
//                "materials/java-dumb.pdf",
//                10f,
//                "PDF",
//                LocalDateTime.now(),
//                MaterialStatus.PENDING
//        );
//        testMaterial.setCategory(category);
//    }

    @BeforeEach
    void setUp() {
        repository = new StudyMaterialRepository();
        roleRepo = new RoleRepository();
        userRepo = new UserRepository();
        categoryRepo = new CategoryRepository();

        Role testRole = roleRepo.findByName(RoleType.STUDENT);
        if (testRole == null) {
            testRole = new Role(RoleType.STUDENT);
            testRole = roleRepo.save(testRole);
        }

        user = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password", testRole);
        user = userRepo.save(user);

        category = new Category("Test Category", user);
        category = categoryRepo.save(category);

        testMaterial = new StudyMaterial(
                user,
                "Java for dummies",
                "Introduction to Java Programming for dummies",
                "materials/java-dumb.pdf",
                10f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.PENDING
        );
        testMaterial.setCategory(category);
    }

    @Test
    void save() {
        StudyMaterial savedMaterial = repository.save(testMaterial);
        assertNotNull(savedMaterial);
        assertNotNull(savedMaterial.getMaterialId());
        assertEquals("Java for dummies", savedMaterial.getName());
        assertEquals("Introduction to Java Programming for dummies", savedMaterial.getDescription());
        assertEquals("materials/java-dumb.pdf", savedMaterial.getLink());
        assertEquals(10f, savedMaterial.getFileSize());
        assertEquals("PDF", savedMaterial.getFileType());
        assertEquals(MaterialStatus.PENDING, savedMaterial.getStatus());
        assertEquals(category.getCategoryId(), savedMaterial.getCategory().getCategoryId());
        assertEquals(user.getUserId(), savedMaterial.getUploader().getUserId());
    }

    @Test
    void findById() {
        StudyMaterial savedMaterial = repository.save(testMaterial);
        StudyMaterial foundMaterial = repository.findById(savedMaterial.getMaterialId());
        assertNotNull(foundMaterial);
        assertEquals(savedMaterial.getMaterialId(), foundMaterial.getMaterialId());
        assertEquals(savedMaterial.getName(), foundMaterial.getName());
        assertEquals(savedMaterial.getDescription(), foundMaterial.getDescription());
        assertEquals(savedMaterial.getLink(), foundMaterial.getLink());
        assertEquals(savedMaterial.getFileSize(), foundMaterial.getFileSize());
        assertEquals(savedMaterial.getFileType(), foundMaterial.getFileType());
        assertEquals(savedMaterial.getStatus(), foundMaterial.getStatus());
        assertEquals(category.getCategoryId(), foundMaterial.getCategory().getCategoryId());
        assertEquals(user.getUserId(), foundMaterial.getUploader().getUserId());
    }
}
