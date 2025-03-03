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
        adminRole.getPermissions().add(new Permission(PermissionType.APPROVE_RESOURCE));
        adminRole.getPermissions().add(new Permission(PermissionType.DELETE_OWN_RESOURCE));
        teacherRole.getPermissions().add(new Permission(PermissionType.DELETE_COURSE_RESOURCE));
        teacherRole.getPermissions().add(new Permission(PermissionType.CREATE_CATEGORY));
        studentRole.getPermissions().add(new Permission(PermissionType.DELETE_OWN_RESOURCE));
        studentRole.getPermissions().add(new Permission(PermissionType.UPDATE_OWN_TAG));

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
        assertTrue(permissionService.hasPermissionOnEntity(adminUser, PermissionType.DELETE_OWN_RESOURCE, 1),
                "Admin should be able to delete their own resource");
    }

    @Test
    void testStudentCannotDeleteAnyResource() {
        assertFalse(permissionService.hasPermissionOnEntity(studentUser, PermissionType.DELETE_ANY_RESOURCE, 1),
                "Student should not be able to delete any resource");
    }

    @Test
    void testTeacherCanDeleteCourseResource() {
        assertTrue(permissionService.hasPermission(teacherUser, PermissionType.DELETE_COURSE_RESOURCE),
                "Teacher should be able to delete course resources");
    }

    @Test
    void testStudentCannotDeleteCourseResource() {
        assertFalse(permissionService.hasPermission(studentUser, PermissionType.DELETE_COURSE_RESOURCE),
                "Student should not be able to delete course resources");
    }

    @Test
    void testAdminCanDeleteAnyResource() {
        assertTrue(permissionService.hasPermission(adminUser, PermissionType.DELETE_ANY_RESOURCE),
                "Admin should be able to delete any resource");
    }

    @Test
    void testNullUserCannotHavePermission() {
        assertFalse(permissionService.hasPermissionOnEntity(null, PermissionType.DELETE_ANY_RESOURCE, adminUser.getUserId()),
                "Null user should not have any permissions");
    }

    @Test
    void testTeacherCanCreateCategory() {
        assertTrue(permissionService.hasPermission(teacherUser, PermissionType.CREATE_CATEGORY),
                "Teacher should be able to create categories");
    }

    @Test
    void testAdminCanApproveResource() {
        assertTrue(permissionService.hasPermission(adminUser, PermissionType.APPROVE_RESOURCE),
                "Admin should be able to approve resources");
    }

    @Test
    void testStudentCannotApproveResource() {
        assertFalse(permissionService.hasPermission(studentUser, PermissionType.APPROVE_RESOURCE),
                "Student should not be able to approve resources");
    }

    @Test
    void testUserCanUpdateOwnTag() {
        assertTrue(permissionService.hasPermissionOnEntity(studentUser, PermissionType.UPDATE_OWN_TAG, studentUser.getUserId()),
                "User should be able to update their own tag");
    }
}
