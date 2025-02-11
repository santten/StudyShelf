package domain.service;

import domain.model.User;
import domain.model.Role;
import domain.model.Permission;
import domain.model.PermissionType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PermissionServiceTest {

    @Test
    public void UserHasPermission() {
        User user = new User("John", "Doe", "john@example.com", "password123");
        Role adminRole = new Role("ADMIN");

        Permission deletePermission = new Permission(PermissionType.DELETE_TAGS);
        Permission editPermission = new Permission(PermissionType.UPDATE_TAGS);

        adminRole.getPermissions().add(deletePermission);
        adminRole.getPermissions().add(editPermission);
        user.getRoles().add(adminRole);

        assertTrue(user.hasPermission(PermissionType.DELETE_TAGS, user.getUserId()), "User should have DELETE_TAGS permission");
        assertTrue(user.hasPermission(PermissionType.UPDATE_TAGS, user.getUserId()), "User should have UPDATE_TAGS permission");
        assertTrue(user.hasPermission(PermissionType.READ_RESOURCES, user.getUserId()), "Everyone should have VIEW_RESOURCES permission");
        assertFalse(user.hasPermission(PermissionType.DELETE_ANY_RESOURCE, user.getUserId()), "User should NOT have DELETE_OTHER_RESOURCES permission");
    }

    @Test
    public void AdminHasAllPermissions() {
        User admin = new User("Admin", "User", "admin@example.com", "password123");
        Role adminRole = new Role("ADMIN");

        for (PermissionType perm : PermissionType.values()) {
            adminRole.getPermissions().add(new Permission(perm));
        }
        admin.getRoles().add(adminRole);
        for (PermissionType perm : PermissionType.values()) {
            assertTrue(admin.hasPermission(perm, admin.getUserId()), "Admin should have permission: " + perm);
        }
    }


    @Test
    public void TeacherHasCorrectPermissions() {
        User teacher = new User("Teacher", "User", "teacher@example.com", "password123");
        Role teacherRole = new Role("TEACHER");

        PermissionType[] allowedPermissions = {
                PermissionType.CREATE_TAGS, PermissionType.UPDATE_TAGS,
                PermissionType.CREATE_CATEGORY, PermissionType.UPDATE_CATEGORY,
                PermissionType.CREATE_RESOURCE, PermissionType.UPDATE_OWN_RESOURCE,
                PermissionType.READ_RESOURCES
        };

        PermissionType[] disallowedPermissions = {
                PermissionType.DELETE_TAGS, PermissionType.DELETE_CATEGORY,
                PermissionType.DELETE_OWN_RESOURCE, PermissionType. DELETE_ANY_RESOURCE
        };

        for (PermissionType perm : allowedPermissions) {
            teacherRole.getPermissions().add(new Permission(perm));
        }

        teacher.getRoles().add(teacherRole);

        for (PermissionType perm : allowedPermissions) {
            assertTrue(teacher.hasPermission(perm, teacher.getUserId()), "Teacher should have permission: " + perm);
        }

        for (PermissionType perm : disallowedPermissions) {
            assertFalse(teacher.hasPermission(perm, teacher.getUserId()), "Teacher should NOT have permission: " + perm);
        }
    }


    @Test
    public void StudentHasCorrectPermissions() {
        User student = new User("Student", "User", "student@example.com", "password123");
        Role studentRole = new Role("STUDENT");

        PermissionType[] allowedPermissions = {
                PermissionType.CREATE_RESOURCE, PermissionType.UPDATE_OWN_RESOURCE,
                PermissionType.DELETE_OWN_RESOURCE, PermissionType.READ_RESOURCES
        };

        PermissionType[] disallowedPermissions = {
                PermissionType.CREATE_TAGS, PermissionType.UPDATE_TAGS, PermissionType.DELETE_TAGS,
                PermissionType.CREATE_CATEGORY, PermissionType.UPDATE_CATEGORY, PermissionType.DELETE_CATEGORY,
                PermissionType.DELETE_ANY_RESOURCE
        };

        for (PermissionType perm : allowedPermissions) {
            studentRole.getPermissions().add(new Permission(perm));
        }

        student.getRoles().add(studentRole);

        for (PermissionType perm : allowedPermissions) {
            assertTrue(student.hasPermission(perm, student.getUserId()), "Student should have permission: " + perm);
        }

        for (PermissionType perm : disallowedPermissions) {
            assertFalse(student.hasPermission(perm, student.getUserId()), "Student should NOT have permission: " + perm);
        }
    }


    @Test
    public void RoleWithoutPermissionsShouldDenyAll() {
        User guest = new User("Guest", "User", "guest@example.com", "password123");
        Role guestRole = new Role("GUEST");

        guest.getRoles().add(guestRole);

        for (PermissionType perm : PermissionType.values()) {
            if (perm == PermissionType.READ_RESOURCES) {
                assertTrue(guest.hasPermission(perm, guest.getUserId()), "Guest should have permission: " + perm);
            } else {
                assertFalse(guest.hasPermission(perm, guest.getUserId()), "Guest should NOT have permission: " + perm);
            }
        }
    }

    @Test
    public void UserCanEditOwnResource() {
        User uploader = new User("Uploader", "User", "uploader@example.com", "password123");
        User viewer = new User("Viewer", "User", "viewer@example.com", "password321");

        Role uploaderRole = new Role("UPLOADER");
        uploaderRole.getPermissions().add(new Permission(PermissionType.UPDATE_OWN_RESOURCE));
        uploader.getRoles().add(uploaderRole);

        assertTrue(uploader.hasPermissionOnResource(PermissionType.UPDATE_OWN_RESOURCE, uploader.getUserId()),
                "Uploader should be able to edit their own resource");

        assertFalse(viewer.hasPermissionOnResource(PermissionType.UPDATE_OWN_RESOURCE, uploader.getUserId()),
                "Viewer should NOT be able to edit uploader's resource");
    }
}
