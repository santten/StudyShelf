package infrastructure.config;

import domain.model.Permission;
import domain.model.PermissionType;
import domain.model.Role;
import domain.model.RoleType;
import infrastructure.repository.PermissionRepository;
import infrastructure.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DatabaseInitializer {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final EntityManager em;

    public DatabaseInitializer() {
        this.roleRepository = new RoleRepository();
        this.permissionRepository = new PermissionRepository();
        this.em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
    }

    public void initializeRolesAndPermissions() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            if (roleRepository.findByName(RoleType.ADMIN) == null) {
                Map<PermissionType, Permission> savedPermissions = new HashMap<>();

                for (PermissionType type : PermissionType.values()) {
                    Permission existingPermission = permissionRepository.findByType(type);
                    if (existingPermission == null) {
                        Permission permission = new Permission(type);
                        savedPermissions.put(type, em.merge(permission));
                    } else {
                        savedPermissions.put(type, existingPermission);
                    }
                }

                Role adminRole = new Role(RoleType.ADMIN);
                Role teacherRole = new Role(RoleType.TEACHER);
                Role studentRole = new Role(RoleType.STUDENT);

                // Admin all permissions
                adminRole.getPermissions().addAll(savedPermissions.values());

                // Teacher permissions
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
                        savedPermissions.get(PermissionType.DELETE_OWN_USER)
                ));

                //Student permissions
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

                roleRepository.save(adminRole);
                roleRepository.save(teacherRole);
                roleRepository.save(studentRole);
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }
}

