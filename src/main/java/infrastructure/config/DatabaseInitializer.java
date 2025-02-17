package infrastructure.config;

import domain.model.Permission;
import domain.model.PermissionType;
import domain.model.Role;
import infrastructure.repository.PermissionRepository;
import infrastructure.repository.RoleRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class DatabaseInitializer {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public DatabaseInitializer() {
        this.roleRepository = new RoleRepository();
        this.permissionRepository = new PermissionRepository();
    }

    public void initializeRolesAndPermissions() {
        if (roleRepository.findByName("ADMIN") == null) {
            // Create and save permissions first
            Map<PermissionType, Permission> savedPermissions = new HashMap<>();
            for (PermissionType type : PermissionType.values()) {
                Permission permission = new Permission(type);
                savedPermissions.put(type, permissionRepository.save(permission));
            }

            // Create roles with saved permissions
            Role adminRole = new Role("ADMIN");
            Role teacherRole = new Role("TEACHER");
            Role studentRole = new Role("STUDENT");

            // Add saved permissions to admin
            adminRole.getPermissions().addAll(savedPermissions.values());

            // Add saved permissions to teacher
            teacherRole.getPermissions().addAll(Arrays.asList(
                    savedPermissions.get(PermissionType.CREATE_TAGS),
                    savedPermissions.get(PermissionType.UPDATE_TAGS),
                    savedPermissions.get(PermissionType.CREATE_CATEGORY),
                    savedPermissions.get(PermissionType.UPDATE_CATEGORY),
                    savedPermissions.get(PermissionType.CREATE_RESOURCE),
                    savedPermissions.get(PermissionType.UPDATE_OWN_RESOURCE),
                    savedPermissions.get(PermissionType.READ_RESOURCES)
            ));

            // Add saved permissions to student
            studentRole.getPermissions().addAll(Arrays.asList(
                    savedPermissions.get(PermissionType.CREATE_RESOURCE),
                    savedPermissions.get(PermissionType.UPDATE_OWN_RESOURCE),
                    savedPermissions.get(PermissionType.DELETE_OWN_RESOURCE),
                    savedPermissions.get(PermissionType.READ_RESOURCES)
            ));

            // Save roles
            roleRepository.save(adminRole);
            roleRepository.save(teacherRole);
            roleRepository.save(studentRole);
        }
    }
}

