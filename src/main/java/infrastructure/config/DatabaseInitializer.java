package infrastructure.config;

import domain.model.*;
import infrastructure.repository.PermissionRepository;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import domain.service.PasswordService;

import java.util.*;

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

//    public void initializeRolesAndPermissions() {
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//        try {
//            Role adminRole = roleRepository.findByName(RoleType.ADMIN);
//            Role teacherRole = roleRepository.findByName(RoleType.TEACHER);
//            Role studentRole = roleRepository.findByName(RoleType.STUDENT);
//
//            if (adminRole == null || teacherRole == null || studentRole == null) {
//                Map<PermissionType, Permission> savedPermissions = new HashMap<>();
//
//                for (PermissionType type : PermissionType.values()) {
//                    Permission permission = new Permission(type);
//                    if (permission == null) {
//                        permission = new Permission(type);
//                        em.persist(permission);
//                    }
//                    savedPermissions.put(type, permission);
//                }
//
//                if (adminRole == null) {
//                    adminRole = new Role(RoleType.ADMIN);
//                    adminRole.getPermissions().addAll(savedPermissions.values());
//                    em.persist(adminRole);
//                } else {
//                    adminRole = em.merge(adminRole);
//                }
//
//                if (teacherRole == null) {
//                    teacherRole = new Role(RoleType.TEACHER);
//                    teacherRole.getPermissions().addAll(Arrays.asList(
//                            savedPermissions.get(PermissionType.CREATE_RESOURCE),
//                            savedPermissions.get(PermissionType.UPDATE_OWN_RESOURCE),
//                            savedPermissions.get(PermissionType.READ_RESOURCES),
//                            savedPermissions.get(PermissionType.DELETE_COURSE_RESOURCE),
//                            savedPermissions.get(PermissionType.CREATE_TAG),
//                            savedPermissions.get(PermissionType.UPDATE_COURSE_TAG),
//                            savedPermissions.get(PermissionType.READ_TAGS),
//                            savedPermissions.get(PermissionType.DELETE_COURSE_TAG),
//                            savedPermissions.get(PermissionType.CREATE_CATEGORY),
//                            savedPermissions.get(PermissionType.UPDATE_COURSE_CATEGORY),
//                            savedPermissions.get(PermissionType.READ_CATEGORIES),
//                            savedPermissions.get(PermissionType.DELETE_COURSE_CATEGORY),
//                            savedPermissions.get(PermissionType.CREATE_REVIEW),
//                            savedPermissions.get(PermissionType.UPDATE_OWN_REVIEW),
//                            savedPermissions.get(PermissionType.READ_REVIEWS),
//                            savedPermissions.get(PermissionType.DELETE_OWN_REVIEW),
//                            savedPermissions.get(PermissionType.CREATE_RATING),
//                            savedPermissions.get(PermissionType.UPDATE_OWN_RATING),
//                            savedPermissions.get(PermissionType.READ_RATINGS),
//                            savedPermissions.get(PermissionType.DELETE_OWN_RATING),
//                            savedPermissions.get(PermissionType.CREATE_USER),
//                            savedPermissions.get(PermissionType.UPDATE_OWN_USER),
//                            savedPermissions.get(PermissionType.READ_OWN_USER),
//                            savedPermissions.get(PermissionType.DELETE_OWN_USER),
//                            savedPermissions.get(PermissionType.APPROVE_RESOURCE),
//                            savedPermissions.get(PermissionType.REJECT_RESOURCE),
//                            savedPermissions.get(PermissionType.REVIEW_PENDING_RESOURCES)
//                    ));
//                    em.persist(teacherRole);
//                } else {
//                    teacherRole = em.merge(teacherRole);
//                }
//
//                //Student permissions
//                if (studentRole == null) {
//                    studentRole = new Role(RoleType.STUDENT);
//                    studentRole.getPermissions().addAll(Arrays.asList(
//                            savedPermissions.get(PermissionType.CREATE_RESOURCE),
//                            savedPermissions.get(PermissionType.UPDATE_OWN_RESOURCE),
//                            savedPermissions.get(PermissionType.READ_RESOURCES),
//                            savedPermissions.get(PermissionType.DELETE_OWN_RESOURCE),
//                            savedPermissions.get(PermissionType.CREATE_TAG),
//                            savedPermissions.get(PermissionType.UPDATE_OWN_TAG),
//                            savedPermissions.get(PermissionType.READ_TAGS),
//                            savedPermissions.get(PermissionType.READ_CATEGORIES),
//                            savedPermissions.get(PermissionType.CREATE_REVIEW),
//                            savedPermissions.get(PermissionType.UPDATE_OWN_REVIEW),
//                            savedPermissions.get(PermissionType.READ_REVIEWS),
//                            savedPermissions.get(PermissionType.DELETE_OWN_REVIEW),
//                            savedPermissions.get(PermissionType.CREATE_RATING),
//                            savedPermissions.get(PermissionType.UPDATE_OWN_RATING),
//                            savedPermissions.get(PermissionType.READ_RATINGS),
//                            savedPermissions.get(PermissionType.DELETE_OWN_RATING),
//                            savedPermissions.get(PermissionType.CREATE_USER),
//                            savedPermissions.get(PermissionType.UPDATE_OWN_USER),
//                            savedPermissions.get(PermissionType.READ_OWN_USER),
//                            savedPermissions.get(PermissionType.DELETE_OWN_USER)
//                    ));
//                    em.persist(studentRole);
//                } else {
//                    studentRole = em.merge(studentRole);
//                }
//
//                em.persist(adminRole);
//                em.persist(teacherRole);
//                em.persist(studentRole);
//                em.flush();
//            }
//
//            User adminUser = userRepository.findByEmail("admin@test.com");
//            if (adminUser == null) {
//                adminUser = new User();
//                adminUser.setFirstName("Admin");
//                adminUser.setLastName("User");
//                adminUser.setEmail("admin@test.com");
//                adminUser.setPassword(passwordService.hashPassword("admin123"));
//                adminUser.setRole(adminRole);
//
//                em.persist(adminUser);
//            }
//
//            tx.commit();
//        } catch (Exception e) {
//            tx.rollback();
//            throw e;
//        }
//    }

    public void initializeRolesAndPermissions() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Map<RoleType, Role> roles = findOrCreateRolesWithPermissions();
            createAdminUserIfNeeded(roles.get(RoleType.ADMIN));
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    private Map<RoleType, Role> findOrCreateRolesWithPermissions() {
        Role adminRole   = roleRepository.findByName(RoleType.ADMIN);
        Role teacherRole = roleRepository.findByName(RoleType.TEACHER);
        Role studentRole = roleRepository.findByName(RoleType.STUDENT);

        if (adminRole != null && teacherRole != null && studentRole != null) {
            return Map.of(
                    RoleType.ADMIN, em.merge(adminRole),
                    RoleType.TEACHER, em.merge(teacherRole),
                    RoleType.STUDENT, em.merge(studentRole)
            );
        }

        Map<PermissionType, Permission> savedPermissions = savePermissionsIfMissing();

        if (adminRole == null) {
            adminRole = createRoleWithPermissions(RoleType.ADMIN, new HashSet<>(savedPermissions.values()));
        }

        if (teacherRole == null) {
            teacherRole = createRoleWithPermissions(RoleType.TEACHER, getTeacherPermissions(savedPermissions));
        }

        if (studentRole == null) {
            studentRole = createRoleWithPermissions(RoleType.STUDENT, getStudentPermissions(savedPermissions));
        }

        return Map.of(
                RoleType.ADMIN, adminRole,
                RoleType.TEACHER, teacherRole,
                RoleType.STUDENT, studentRole
        );
    }

    private Map<PermissionType, Permission> savePermissionsIfMissing() {
        Map<PermissionType, Permission> saved = new EnumMap<>(PermissionType.class);
        for (PermissionType type : PermissionType.values()) {
            Permission p = new Permission(type);
            em.persist(p);
            saved.put(type, p);
        }
        return saved;
    }

    private Role createRoleWithPermissions(RoleType roleType, Set<Permission> permissions) {
        Role role = new Role(roleType);
        role.getPermissions().addAll(permissions);
        em.persist(role);
        return role;
    }

    private Set<Permission> getTeacherPermissions(Map<PermissionType, Permission> p) {
        return Set.of(
                p.get(PermissionType.CREATE_RESOURCE),
                p.get(PermissionType.UPDATE_OWN_RESOURCE),
                p.get(PermissionType.READ_RESOURCES),
                p.get(PermissionType.DELETE_COURSE_RESOURCE),

                p.get(PermissionType.CREATE_TAG),
                p.get(PermissionType.UPDATE_COURSE_TAG),
                p.get(PermissionType.READ_TAGS),
                p.get(PermissionType.DELETE_COURSE_TAG),

                p.get(PermissionType.CREATE_CATEGORY),
                p.get(PermissionType.UPDATE_COURSE_CATEGORY),
                p.get(PermissionType.READ_CATEGORIES),
                p.get(PermissionType.DELETE_COURSE_CATEGORY),

                p.get(PermissionType.CREATE_REVIEW),
                p.get(PermissionType.UPDATE_OWN_REVIEW),
                p.get(PermissionType.READ_REVIEWS),
                p.get(PermissionType.DELETE_OWN_REVIEW),

                p.get(PermissionType.CREATE_RATING),
                p.get(PermissionType.UPDATE_OWN_RATING),
                p.get(PermissionType.READ_RATINGS),
                p.get(PermissionType.DELETE_OWN_RATING),

                p.get(PermissionType.CREATE_USER),
                p.get(PermissionType.UPDATE_OWN_USER),
                p.get(PermissionType.READ_OWN_USER),
                p.get(PermissionType.DELETE_OWN_USER),

                p.get(PermissionType.APPROVE_RESOURCE),
                p.get(PermissionType.REJECT_RESOURCE),
                p.get(PermissionType.REVIEW_PENDING_RESOURCES)
        );
    }

    private Set<Permission> getStudentPermissions(Map<PermissionType, Permission> p) {
        return Set.of(
                p.get(PermissionType.CREATE_RESOURCE),
                p.get(PermissionType.UPDATE_OWN_RESOURCE),
                p.get(PermissionType.READ_RESOURCES),
                p.get(PermissionType.DELETE_OWN_RESOURCE),

                p.get(PermissionType.CREATE_TAG),
                p.get(PermissionType.UPDATE_OWN_TAG),
                p.get(PermissionType.READ_TAGS),

                p.get(PermissionType.READ_CATEGORIES),

                p.get(PermissionType.CREATE_REVIEW),
                p.get(PermissionType.UPDATE_OWN_REVIEW),
                p.get(PermissionType.READ_REVIEWS),
                p.get(PermissionType.DELETE_OWN_REVIEW),

                p.get(PermissionType.CREATE_RATING),
                p.get(PermissionType.UPDATE_OWN_RATING),
                p.get(PermissionType.READ_RATINGS),
                p.get(PermissionType.DELETE_OWN_RATING),

                p.get(PermissionType.CREATE_USER),
                p.get(PermissionType.UPDATE_OWN_USER),
                p.get(PermissionType.READ_OWN_USER),
                p.get(PermissionType.DELETE_OWN_USER)
        );
    }

    private void createAdminUserIfNeeded(Role adminRole) {
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
    }








    public static void main(String[] args) {
        DatabaseInitializer dbInit = new DatabaseInitializer();
        dbInit.initializeRolesAndPermissions();
    }
}

