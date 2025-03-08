package infrastructure.repository;

import domain.model.*;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudyMaterialRepositoryTest {
    private static EntityManagerFactory emf;
    private StudyMaterialRepository repository;
    private User user;
    private Category category;
    private StudyMaterial testMaterial;
    private RoleRepository roleRepo;
    private UserRepository userRepo;
    private CategoryRepository categoryRepo;

    @BeforeAll
    static void setupDatabase() {
        emf = TestPersistenceUtil.getEntityManagerFactory();
    }

    @BeforeEach
    void setUp() {
        repository = new StudyMaterialRepository(emf);
        roleRepo = new RoleRepository(emf);
        userRepo = new UserRepository(emf);
        categoryRepo = new CategoryRepository(emf);

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
        testMaterial = repository.save(testMaterial);
    }

    @Test
    void save() {
        assertNotNull(testMaterial);
        assertNotNull(testMaterial.getMaterialId());
        assertEquals("Java for dummies", testMaterial.getName());
        assertEquals("Introduction to Java Programming for dummies", testMaterial.getDescription());
        assertEquals("materials/java-dumb.pdf", testMaterial.getLink());
        assertEquals(10f, testMaterial.getFileSize());
        assertEquals("PDF", testMaterial.getFileType());
        assertEquals(MaterialStatus.PENDING, testMaterial.getStatus());
        assertEquals(category.getCategoryId(), testMaterial.getCategory().getCategoryId());
        assertEquals(user.getUserId(), testMaterial.getUploader().getUserId());
    }

    @Test
    void findById() {
        StudyMaterial foundMaterial = repository.findById(testMaterial.getMaterialId());
        assertNotNull(foundMaterial);
        assertEquals(testMaterial.getMaterialId(), foundMaterial.getMaterialId());
    }

    @Test
    void updateMaterialStatus() {
        repository.updateMaterialStatus(testMaterial.getMaterialId(), MaterialStatus.APPROVED);
        StudyMaterial updatedMaterial = repository.findById(testMaterial.getMaterialId());
        assertNotNull(updatedMaterial);
        assertEquals(MaterialStatus.APPROVED, updatedMaterial.getStatus());
    }

    @Test
    void deleteMaterial() {
        repository.delete(testMaterial);

        StudyMaterial deletedMaterial = repository.findById(testMaterial.getMaterialId());
        assertNull(deletedMaterial);
    }

    @Test
    void findByStatus() {
        StudyMaterial pendingMaterial = new StudyMaterial(
                user,
                "Pending Material",
                "This is a pending study material",
                "materials/pending.pdf",
                5.0f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.PENDING
        );
        pendingMaterial.setCategory(category);
        pendingMaterial = repository.save(pendingMaterial);

        StudyMaterial approvedMaterial = new StudyMaterial(
                user,
                "Approved Material",
                "This is an approved study material",
                "materials/approved.pdf",
                7.5f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.APPROVED
        );
        approvedMaterial.setCategory(category);
        approvedMaterial = repository.save(approvedMaterial);

        StudyMaterial rejectedMaterial = new StudyMaterial(
                user,
                "Rejected Material",
                "This is a rejected study material",
                "materials/rejected.pdf",
                3.2f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.REJECTED
        );
        rejectedMaterial.setCategory(category);
        rejectedMaterial = repository.save(rejectedMaterial);

        List<StudyMaterial> pendingMaterials = repository.findByStatus(MaterialStatus.PENDING);
        assertFalse(pendingMaterials.isEmpty());
        assertTrue(pendingMaterials.stream().allMatch(m -> m.getStatus() == MaterialStatus.PENDING));

        List<StudyMaterial> approvedMaterials = repository.findByStatus(MaterialStatus.APPROVED);
//        assertEquals(1, approvedMaterials.size());
        assertEquals(MaterialStatus.APPROVED, approvedMaterials.get(0).getStatus());

        List<StudyMaterial> rejectedMaterials = repository.findByStatus(MaterialStatus.REJECTED);
//        assertEquals(1, rejectedMaterials.size());
        assertEquals(MaterialStatus.REJECTED, rejectedMaterials.get(0).getStatus());

        repository.updateMaterialStatus(pendingMaterial.getMaterialId(), MaterialStatus.APPROVED);
        List<StudyMaterial> updatedApprovedMaterials = repository.findByStatus(MaterialStatus.APPROVED);

//        assertEquals(2, updatedApprovedMaterials.size());
        assertTrue(updatedApprovedMaterials.stream().allMatch(m -> m.getStatus() == MaterialStatus.APPROVED));
    }

    @AfterAll
    static void tearDown() {
        TestPersistenceUtil.closeEntityManagerFactory();
    }
}
