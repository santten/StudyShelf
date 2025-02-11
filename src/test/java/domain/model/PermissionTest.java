package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void testPermissionCreation() {
        Permission permission = new Permission(PermissionType.CREATE_TAGS);
        assertEquals(PermissionType.CREATE_TAGS, permission.getName());
    }

    @Test
    void testPermissionEquality() {
        Permission perm1 = new Permission(PermissionType.UPDATE_OWN_RESOURCE);
        Permission perm2 = new Permission(PermissionType.UPDATE_OWN_RESOURCE);
        assertEquals(perm1, perm2);
    }

    @Test
    void testPermissionToString() {
        Permission permission = new Permission(PermissionType.READ_RESOURCES);
        assertEquals("Permission{id=null, name=READ_RESOURCES}", permission.toString());
    }
}
