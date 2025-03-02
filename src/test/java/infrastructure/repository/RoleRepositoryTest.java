//package infrastructure.repository;
//
//import domain.model.*;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.*;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class RoleRepositoryTest {
//    private RoleRepository roleRepository;
//    private PermissionRepository permissionRepository;
//
//    @BeforeAll
//    void setup() {
//        roleRepository = new RoleRepository();
//        permissionRepository = new PermissionRepository();
//    }
//
//    @BeforeEach
//    void cleanDatabase() {
//        EntityManager em = roleRepository.getEntityManager();
//        em.getTransaction().begin();
//
//        em.createQuery("DELETE FROM User u WHERE u.role IN (SELECT r FROM Role r)").executeUpdate();
//
//        for (RoleType roleType : RoleType.values()) {
//            Role existingRole = roleRepository.findByName(roleType);
//            if (existingRole != null) {
//                roleRepository.delete(existingRole);
//            }
//        }
//
//        em.getTransaction().commit();
//        em.close();
//    }
//
//
//    @Test
//    void testSaveRole() {
//        Role role = new Role(RoleType.STUDENT);
//        Role savedRole = roleRepository.save(role);
//
//        assertNotNull(savedRole);
//        assertNotNull(savedRole.getId());
//        assertEquals(RoleType.STUDENT, savedRole.getName());
//    }
//
//    @Test
//    void testFindByName() {
//        Role role = new Role(RoleType.TEACHER);
//        roleRepository.save(role);
//
//        Role foundRole = roleRepository.findByName(RoleType.TEACHER);
//        assertNotNull(foundRole);
//        assertEquals(RoleType.TEACHER, foundRole.getName());
//    }
//
//    @AfterAll
//    void teardown() {
//        roleRepository = null;
//        permissionRepository = null;
//    }
//}
