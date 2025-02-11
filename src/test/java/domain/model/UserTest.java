package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Role adminRole;
    private Permission permission;

    @BeforeEach
    void setUp() {
        user = new User("John", "Doe", "john@example.com", "password");
        adminRole = new Role("ADMIN");
        permission = new Permission(PermissionType.DELETE_ANY_RESOURCE);
    }

    @Test
    void testUserCreation() {
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void testUserHasPermission() {
        adminRole.getPermissions().add(permission);
        user.getRoles().add(adminRole);

        assertTrue(user.hasPermission(PermissionType.DELETE_ANY_RESOURCE, user.getUserId()));
        assertFalse(user.hasPermission(PermissionType.UPDATE_OWN_RESOURCE, user.getUserId()));
    }

    @Test
    void testUserWithoutPermissionsShouldDenyAll() {
        User guest = new User("Guest", "User", "guest@example.com", "password");
        assertFalse(guest.hasPermission(PermissionType.CREATE_TAGS, guest.getUserId()));
        assertFalse(guest.hasPermission(PermissionType.READ_RESOURCES, guest.getUserId()));
    }

    @Test
    void testUserCanEditOwnResource() {
        User uploader = new User("Uploader", "User", "uploader@example.com", "password");
        Role uploaderRole = new Role("UPLOADER");
        uploaderRole.getPermissions().add(new Permission(PermissionType.UPDATE_OWN_RESOURCE));
        uploader.getRoles().add(uploaderRole);

        // Uploader can edit their own resource
        assertTrue(uploader.hasPermissionOnResource(PermissionType.UPDATE_OWN_RESOURCE, uploader.getUserId()));

        // Another user cannot edit uploader's resource
        User viewer = new User("Viewer", "User", "viewer@example.com", "password");
        assertFalse(viewer.hasPermissionOnResource(PermissionType.UPDATE_OWN_RESOURCE, uploader.getUserId()));
    }
}
