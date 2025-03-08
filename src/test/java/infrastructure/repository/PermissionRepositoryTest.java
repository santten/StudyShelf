package infrastructure.repository;

import domain.model.Permission;
import domain.model.PermissionType;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PermissionRepositoryTest {
    private EntityManager entityManager;
    private PermissionRepository permissionRepository;
    private Permission testPermission;

    @BeforeAll
    void setupDatabase() {
        entityManager = TestPersistenceUtil.getEntityManager();
        EntityManagerFactory testEmf = TestPersistenceUtil.getEntityManagerFactory();
        permissionRepository = new PermissionRepository(testEmf);
    }

    @BeforeEach
    void setUp() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }

        entityManager.createQuery("DELETE FROM Permission").executeUpdate();

        testPermission = new Permission(PermissionType.CREATE_RESOURCE);
        entityManager.persist(testPermission);

        entityManager.getTransaction().commit();
    }

    @Test
    void testSave() {
        Permission newPermission = new Permission(PermissionType.READ_RESOURCES);

        entityManager.getTransaction().begin();
        Permission savedPermission = permissionRepository.save(newPermission);
        entityManager.flush();
        entityManager.getTransaction().commit();

        assertNotNull(savedPermission);
        assertNotNull(savedPermission.getId());
        assertEquals(PermissionType.READ_RESOURCES, savedPermission.getName());
    }

    @Test
    void testFindById() {
        entityManager.getTransaction().begin();
        Permission foundPermission = permissionRepository.findByName(testPermission.getName());
        entityManager.getTransaction().commit();

        assertNotNull(foundPermission);
        assertEquals(testPermission.getId(), foundPermission.getId());
        assertEquals(testPermission.getName(), foundPermission.getName());
    }

    @Test
    void testFindAll() {
        List<Permission> permissions = permissionRepository.findAll();

        assertFalse(permissions.isEmpty(), "Permissions list should not be empty");

        assertTrue(permissions.stream()
                .anyMatch(p -> p.getId() == testPermission.getId()),
                "Permissions list should contain testPermission");
    }

    @Test
    void testFindByName() {
        Permission foundPermission = permissionRepository.findByName(testPermission.getName());
        assertNotNull(foundPermission);
        assertEquals(testPermission.getId(), foundPermission.getId());
    }


    @Test
    void testDelete() {
        entityManager.getTransaction().begin();
        permissionRepository.delete(testPermission.getId());
        entityManager.flush();  // **确保删除操作提交**
        entityManager.getTransaction().commit();

        Permission deletedPermission = permissionRepository.findById(testPermission.getId());
        assertNull(deletedPermission, "Deleted permission should be null");
    }

    @AfterAll
    void tearDown() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
