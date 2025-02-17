package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class PermissionTest {
    private Permission permission;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        permission = new Permission(PermissionType.CREATE_RESOURCE);
        adminRole = new Role(RoleType.ADMIN);
        userRole = new Role(RoleType.STUDENT);
    }

    @Test
    void testPermissionCreation() {
        assertNotNull(permission);
        assertEquals(PermissionType.CREATE_RESOURCE, permission.getName());
    }

    @Test
    void testGetRolesIsEmptyInitially() {
        assertNotNull(permission.getRoles());
        assertTrue(permission.getRoles().isEmpty());
    }

    @Test
    void testAddRoles() {
        permission.getRoles().add(adminRole);
        permission.getRoles().add(userRole);

        assertEquals(2, permission.getRoles().size());
        assertTrue(permission.getRoles().contains(adminRole));
        assertTrue(permission.getRoles().contains(userRole));
    }

    @Test
    void testPermissionEqualsAndHashCode() {
        Permission perm1 = new Permission(PermissionType.UPDATE_OWN_RESOURCE);
        Permission perm2 = new Permission(PermissionType.UPDATE_OWN_RESOURCE);
        Permission perm3 = new Permission(PermissionType.CREATE_TAG);

        assertEquals(perm1.getName(), perm2.getName());
        assertNotEquals(perm1.getName(), perm3.getName());
    }
}
