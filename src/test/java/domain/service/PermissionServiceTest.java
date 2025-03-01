package domain.service;

import domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionServiceTest {
    private PermissionService permissionService;
    private User adminUser;
    private User teacherUser;
    private User studentUser;
    private Role adminRole;
    private Role teacherRole;
    private Role studentRole;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionService();

        adminRole = new Role(RoleType.ADMIN);
        teacherRole = new Role(RoleType.TEACHER);
        studentRole = new Role(RoleType.STUDENT);

        adminRole.getPermissions().add(new Permission(PermissionType.DELETE_ANY_RESOURCE));
        adminRole.getPermissions().add(new Permission(PermissionType.DELETE_OWN_RESOURCE));
        teacherRole.getPermissions().add(new Permission(PermissionType.DELETE_COURSE_RESOURCE));
        studentRole.getPermissions().add(new Permission(PermissionType.DELETE_OWN_RESOURCE));


        adminUser = new User(1, "Alice", "Smith", "alice@example.com", "password123", adminRole);
        teacherUser = new User(2, "Charlie", "Brown", "charlie@example.com", "password123", teacherRole);
        studentUser = new User(3, "Bob", "Jones", "bob@example.com", "password123", studentRole);
    }

    @Test
    void testAdminHasDeletePermission() {
        assertTrue(permissionService.hasPermission(adminUser, PermissionType.DELETE_ANY_RESOURCE),
                "Admin should be able to delete any resource");
        assertFalse(permissionService.hasPermission(studentUser, PermissionType.DELETE_ANY_RESOURCE),
                "Student should not be able to delete any resource");
    }

    @Test
    void testAdminCanDeleteOwnResource() {
        boolean result = permissionService.hasPermissionOnEntity(adminUser, PermissionType.DELETE_OWN_RESOURCE, 1);
        assertTrue(result, "Admin should be able to delete their own resource");
    }

    @Test
    void testStudentCannotDeleteAnyResource() {
        boolean result = permissionService.hasPermissionOnEntity(studentUser, PermissionType.DELETE_ANY_RESOURCE, 1);
        assertFalse(result, "Student should not be able to delete any resource");
    }

    @Test
    void testTeacherCanDeleteCourseResource() {
        assertTrue(permissionService.hasPermission(teacherUser, PermissionType.DELETE_COURSE_RESOURCE),
                "Teacher should be able to delete course resources");
    }

    @Test
    void testStudentCannotDeleteCourseResource() {
        boolean result = permissionService.hasPermission(studentUser, PermissionType.DELETE_COURSE_RESOURCE);
        assertFalse(result, "Student should not be able to delete course resources");
    }

    @Test
    void testAdminCanDeleteAnyResource() {
        boolean result = permissionService.hasPermission(adminUser, PermissionType.DELETE_ANY_RESOURCE);
        assertTrue(result, "Admin should be able to delete any resource");
    }

    @Test
    void testNullUserCannotHavePermission() {
        boolean result = permissionService.hasPermissionOnEntity(null, PermissionType.DELETE_ANY_RESOURCE, adminUser.getUserId());
        assertFalse(result, "Null user should not have any permissions");
    }
}
