package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

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

    @BeforeEach
    void testUserDefaultConstructor() {
        User defaultUser = new User();
        assertNotNull(defaultUser);
    }

    @Test
    void testUserCreation() {
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
    }

    @Test
    void testSetUserId() {
        user.setUserId(123);
        assertEquals(123, user.getUserId());
    }


    @Test
    void testUserSettersAndGetters() {
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane@example.com");
        user.setPassword("newpass");

        assertEquals("Jane", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("newpass", user.getPassword());
    }

    @Test
    void testUserParameterizedConstructor() {
        User paramUser = new User(1, "Alice", "Doe", "alice@example.com", "123456qwq");
        assertEquals(1, paramUser.getUserId());
        assertEquals("Alice", paramUser.getFirstName());
        assertEquals("Doe", paramUser.getLastName());
        assertEquals("alice@example.com", paramUser.getEmail());
        assertEquals("123456qwq", paramUser.getPassword());
    }

    @Test
    void testUserHasPermission() {
        adminRole.getPermissions().add(permission);
        user.setRole(adminRole);

        assertTrue(user.hasPermission(PermissionType.DELETE_ANY_RESOURCE, user.getUserId()));
        assertFalse(user.hasPermission(PermissionType.UPDATE_OWN_RESOURCE, user.getUserId()));
    }

    @Test
    void testGuestUserHasOnlyReadPermission() {
        User guest = new User("Guest", "User", "guest@example.com", "password");
        Role guestRole = new Role("GUEST");
        guest.setRole(guestRole);
        assertFalse(guest.hasPermission(PermissionType.CREATE_TAGS, guest.getUserId()));
        assertTrue(guest.hasPermission(PermissionType.READ_RESOURCES, guest.getUserId()));
    }

    @Test
    void testUserCanEditOwnResource() {
        User uploader = new User("Uploader", "User", "uploader@example.com", "password");
        Role uploaderRole = new Role("UPLOADER");
        uploaderRole.getPermissions().add(new Permission(PermissionType.UPDATE_OWN_RESOURCE));
        uploader.setRole(uploaderRole);

        assertTrue(uploader.hasPermissionOnResource(PermissionType.UPDATE_OWN_RESOURCE, uploader.getUserId()));
    }

    @Test
    void testUserCannotEditOtherUsersResource() {
        User owner = new User("Owner", "User", "owner@example.com", "password");
        User otherUser = new User("Other", "User", "other@example.com", "password");
        Role userRole = new Role("USER");
        owner.setRole(userRole);
        otherUser.setRole(userRole);
        owner.setUserId(1);
        otherUser.setUserId(2);
        assertFalse(otherUser.hasPermissionOnResource(PermissionType.UPDATE_OWN_RESOURCE, owner.getUserId()));
    }

    @Test
    void testUserCannotEditOtherUsersResourceWithPermission() {
        User owner = new User("Owner", "User", "owner@example.com", "password");
        User otherUser = new User("Other", "User", "other@example.com", "password");

        owner.setUserId(1);
        otherUser.setUserId(2);

        Role role = new Role("STUDENT");
        role.getPermissions().add(new Permission(PermissionType.UPDATE_OWN_RESOURCE));
        otherUser.setRole(role);

        assertFalse(otherUser.hasPermissionOnResource(PermissionType.UPDATE_OWN_RESOURCE, owner.getUserId()));
    }



    @Test
    void testUserGetRole() {
        assertNull(user.getRole());
        user.setRole(adminRole);
        assertEquals(adminRole, user.getRole());
    }

    @Test
    void testUserGetPermissions() {
        Role role = new Role("TEST_ROLE");
        role.getPermissions().add(new Permission(PermissionType.CREATE_TAGS));
        user.setRole(role);

        Set<Permission> permissions = user.getPermissions();
        assertFalse(permissions.isEmpty());
        assertTrue(permissions.stream().anyMatch(p -> p.getName() == PermissionType.CREATE_TAGS));
    }

}

