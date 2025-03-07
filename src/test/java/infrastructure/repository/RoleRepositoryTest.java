package infrastructure.repository;

import domain.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import util.TestPersistenceUtil;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoleRepositoryTest {
    private EntityManagerFactory emf;
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;

    @BeforeAll
    void setup() {
        emf = TestPersistenceUtil.getEntityManagerFactory();
        roleRepository = new RoleRepository(emf);
        permissionRepository = new PermissionRepository(emf);
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = TestPersistenceUtil.getEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM User u WHERE u.role IN (SELECT r FROM Role r)").executeUpdate();
        for (RoleType roleType : RoleType.values()) {
            Role existingRole = roleRepository.findByName(roleType);
            if (existingRole != null) {
                roleRepository.delete(existingRole);
            }
        }
        em.getTransaction().commit();
        em.close();
    }



    @Test
    void testSaveRole() {
        Role role = new Role(RoleType.STUDENT);
        Role savedRole = roleRepository.save(role);

        assertNotNull(savedRole);
        assertNotNull(savedRole.getId());
        assertEquals(RoleType.STUDENT, savedRole.getName());
    }
    @Test
    void testSaveRoleWithPermissions() {
        EntityManager em = TestPersistenceUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Permission permission = new Permission(PermissionType.READ_RESOURCES);
            em.persist(permission);

            Role role = new Role(RoleType.TEACHER);
            role.getPermissions().add(permission);
            em.persist(role);

            em.getTransaction().commit();

            em.clear();

            Role retrievedRole = em.find(Role.class, role.getId());
            assertNotNull(retrievedRole);
            assertFalse(retrievedRole.getPermissions().isEmpty());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }


    @Test
    void testFindByName() {
        Role role = new Role(RoleType.ADMIN);
        roleRepository.save(role);

        Role foundRole = roleRepository.findByName(RoleType.ADMIN);

        assertNotNull(foundRole);
        assertEquals(RoleType.ADMIN, foundRole.getName());
    }

    @Test
    void testFindByNameNonExistent() {
        // Delete all roles
        cleanDatabase();

        Role nonExistentRole = roleRepository.findByName(RoleType.STUDENT);
        assertNull(nonExistentRole);
    }

    @Test
    void testSaveExistingRole() {
        Role role1 = new Role(RoleType.TEACHER);
        Role savedRole1 = roleRepository.save(role1);
        Role role2 = new Role(RoleType.TEACHER);
        Role savedRole2 = roleRepository.save(role2);
        assertEquals(savedRole1.getId(), savedRole2.getId());
    }

    @AfterAll
    void teardown() {
        TestPersistenceUtil.closeEntityManagerFactory();
    }
}
