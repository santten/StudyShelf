package domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        Role role = new Role(RoleType.STUDENT);
        User user = new User("Alice", "Smith", "alice@example.com", "password123", role);

        assertNotNull(user);
        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(role, user.getRole());
    }

    @Test
    void testUserHasPermission() {
        Role role = new Role(RoleType.ADMIN);
        Permission permission = new Permission(PermissionType.DELETE_ANY_USER);
        role.getPermissions().add(permission);

        User user = new User("John", "Doe", "john@example.com", "securePass", role);

        assertTrue(user.hasPermission(PermissionType.DELETE_ANY_USER));
        assertFalse(user.hasPermission(PermissionType.CREATE_RESOURCE));
    }

    @Test
    void testUserWithoutRole() {
        User user = new User("NoRole", "User", "norole@example.com", "nopassword", null);

        assertFalse(user.hasPermission(PermissionType.READ_RESOURCES));
    }
}


