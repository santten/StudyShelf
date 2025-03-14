package infrastructure.config;

import domain.model.*;
import infrastructure.repository.PermissionRepository;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import domain.service.PasswordService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DatabaseInitializer {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final EntityManager em;
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public DatabaseInitializer() {
        this.roleRepository = new RoleRepository();
        this.permissionRepository = new PermissionRepository();
        this.userRepository = new UserRepository();
        this.passwordService = new PasswordService();
        this.em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
    }

    public void initializeRolesAndPermissions() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Role adminRole = roleRepository.findByName(RoleType.ADMIN);
            Role teacherRole = roleRepository.findByName(RoleType.TEACHER);
            Role studentRole = roleRepository.findByName(RoleType.STUDENT);

            if (adminRole == null || teacherRole == null || studentRole == null) {
                Map<PermissionType, Permission> savedPermissions = new HashMap<>();

                for (PermissionType type : PermissionType.values()) {
                    Permission permission = new Permission(type);
                    if (permission == null) {
                        permission = new Permission(type);
                        em.persist(permission);
                    }
                    savedPermissions.put(type, permission);
                }

                if (adminRole == null) {
                    adminRole = new Role(RoleType.ADMIN);
                    adminRole.getPermissions().addAll(savedPermissions.values());
                    em.persist(adminRole);
                } else {
                    adminRole = em.merge(adminRole);
                }

                if (teacherRole == null) {
                    teacherRole = new Role(RoleType.TEACHER);
                    teacherRole.getPermissions().addAll(Arrays.asList(
                            savedPermissions.get(PermissionType.CREATE_RESOURCE),
                            savedPermissions.get(PermissionType.UPDATE_OWN_RESOURCE),
                            savedPermissions.get(PermissionType.READ_RESOURCES),
                            savedPermissions.get(PermissionType.DELETE_COURSE_RESOURCE),
                            savedPermissions.get(PermissionType.CREATE_TAG),
                            savedPermissions.get(PermissionType.UPDATE_COURSE_TAG),
                            savedPermissions.get(PermissionType.READ_TAGS),
                            savedPermissions.get(PermissionType.DELETE_COURSE_TAG),
                            savedPermissions.get(PermissionType.CREATE_CATEGORY),
                            savedPermissions.get(PermissionType.UPDATE_COURSE_CATEGORY),
                            savedPermissions.get(PermissionType.READ_CATEGORIES),
                            savedPermissions.get(PermissionType.DELETE_COURSE_CATEGORY),
                            savedPermissions.get(PermissionType.CREATE_REVIEW),
                            savedPermissions.get(PermissionType.UPDATE_OWN_REVIEW),
                            savedPermissions.get(PermissionType.READ_REVIEWS),
                            savedPermissions.get(PermissionType.DELETE_OWN_REVIEW),
                            savedPermissions.get(PermissionType.CREATE_RATING),
                            savedPermissions.get(PermissionType.UPDATE_OWN_RATING),
                            savedPermissions.get(PermissionType.READ_RATINGS),
                            savedPermissions.get(PermissionType.DELETE_OWN_RATING),
                            savedPermissions.get(PermissionType.CREATE_USER),
                            savedPermissions.get(PermissionType.UPDATE_OWN_USER),
                            savedPermissions.get(PermissionType.READ_OWN_USER),
                            savedPermissions.get(PermissionType.DELETE_OWN_USER),
                            savedPermissions.get(PermissionType.APPROVE_RESOURCE),
                            savedPermissions.get(PermissionType.REJECT_RESOURCE),
                            savedPermissions.get(PermissionType.REVIEW_PENDING_RESOURCES)
                    ));
                    em.persist(teacherRole);
                } else {
                    teacherRole = em.merge(teacherRole);
                }

                //Student permissions
                if (studentRole == null) {
                    studentRole = new Role(RoleType.STUDENT);
                    studentRole.getPermissions().addAll(Arrays.asList(
                            savedPermissions.get(PermissionType.CREATE_RESOURCE),
                            savedPermissions.get(PermissionType.UPDATE_OWN_RESOURCE),
                            savedPermissions.get(PermissionType.READ_RESOURCES),
                            savedPermissions.get(PermissionType.DELETE_OWN_RESOURCE),
                            savedPermissions.get(PermissionType.CREATE_TAG),
                            savedPermissions.get(PermissionType.UPDATE_OWN_TAG),
                            savedPermissions.get(PermissionType.READ_TAGS),
                            savedPermissions.get(PermissionType.READ_CATEGORIES),
                            savedPermissions.get(PermissionType.CREATE_REVIEW),
                            savedPermissions.get(PermissionType.UPDATE_OWN_REVIEW),
                            savedPermissions.get(PermissionType.READ_REVIEWS),
                            savedPermissions.get(PermissionType.DELETE_OWN_REVIEW),
                            savedPermissions.get(PermissionType.CREATE_RATING),
                            savedPermissions.get(PermissionType.UPDATE_OWN_RATING),
                            savedPermissions.get(PermissionType.READ_RATINGS),
                            savedPermissions.get(PermissionType.DELETE_OWN_RATING),
                            savedPermissions.get(PermissionType.CREATE_USER),
                            savedPermissions.get(PermissionType.UPDATE_OWN_USER),
                            savedPermissions.get(PermissionType.READ_OWN_USER),
                            savedPermissions.get(PermissionType.DELETE_OWN_USER)
                    ));
                    em.persist(studentRole);
                } else {
                    studentRole = em.merge(studentRole);
                }

                em.persist(adminRole);
                em.persist(teacherRole);
                em.persist(studentRole);
                em.flush();
            }

            User adminUser = userRepository.findByEmail("admin@test.com");
            if (adminUser == null) {
                adminUser = new User();
                adminUser.setFirstName("Admin");
                adminUser.setLastName("User");
                adminUser.setEmail("admin@test.com");
                adminUser.setPassword(passwordService.hashPassword("admin123"));
                adminUser.setRole(adminRole);

                em.persist(adminUser);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    public static void main(String[] args) {
        DatabaseInitializer dbInit = new DatabaseInitializer();
        dbInit.initializeRolesAndPermissions();
    }
}

