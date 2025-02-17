package domain.service;

import domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionServiceTest {
    private PermissionService permissionService;
    private User adminUser;
    private User studentUser;
    private Role adminRole;
    private Role studentRole;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionService();
        adminRole = new Role(RoleType.ADMIN);
        studentRole = new Role(RoleType.STUDENT);
        Permission deleteAnyPermission = new Permission(PermissionType.DELETE_ANY_RESOURCE);
        Permission deleteOwnPermission = new Permission(PermissionType.DELETE_OWN_RESOURCE);
        adminRole.getPermissions().add(deleteAnyPermission);
        adminRole.getPermissions().add(deleteOwnPermission);
        studentRole.getPermissions().clear();
        adminUser = new User("Alice", "Smith", "alice@example.com", "password123", adminRole);
        studentUser = new User("Bob", "Jones", "bob@example.com", "password123", studentRole);
    }

    @Test
    void testAdminHasDeletePermission() {
        assertTrue(permissionService.hasPermission(adminUser, PermissionType.DELETE_ANY_RESOURCE));
        assertFalse(permissionService.hasPermission(studentUser, PermissionType.DELETE_ANY_RESOURCE));
    }

    @Test
    void testHasPermissionOnResource_AdminCanDeleteOwnResource() {
        boolean result = permissionService.hasPermissionOnResource(adminUser, "DELETE_OWN_RESOURCE", adminUser.getUserId());
        assertTrue(result, "Admin should be able to delete their own resource");
    }

    @Test
    void testHasPermissionOnResource_StudentCannotDeleteAnyResource() {
        boolean result = permissionService.hasPermissionOnResource(studentUser, "DELETE_ANY_RESOURCE", adminUser.getUserId());
        assertFalse(result, "Student should not be able to delete any resource");
    }

    @Test
    void testInvalidPermissionName() {
        boolean result = permissionService.hasPermissionOnResource(adminUser, "INVALID_PERMISSION", adminUser.getUserId());
        assertFalse(result, "Invalid permission name should return false");
    }

    @Test
    void testNullUserCannotHavePermission() {
        boolean result = permissionService.hasPermissionOnResource(null, "DELETE_ANY_RESOURCE", adminUser.getUserId());
        assertFalse(result, "Null user should not have any permissions");
    }
}
