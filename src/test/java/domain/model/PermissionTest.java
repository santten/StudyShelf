package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

class PermissionTest {

    @BeforeEach
    void testDefaultConstructor() {
        Permission permission = new Permission();
        assertNotNull(permission);
    }

    @Test
    void testPermissionCreation() {
        Permission permission = new Permission(PermissionType.CREATE_RESOURCE);
        assertNotNull(permission);
        assertEquals(PermissionType.CREATE_RESOURCE, permission.getName());
    }

    @Test
    void testSetRoles() {
        Permission permission = new Permission(PermissionType.DELETE_ANY_RESOURCE);
        Role role = new Role(RoleType.ADMIN);

        permission.setRoles(Set.of(role));

        assertNotNull(permission.getRoles());
        assertTrue(permission.getRoles().contains(role));
    }



//    @Test
//    void testSetName() {
//        Permission permission = new Permission(PermissionType.UPDATE_OWN_RESOURCE);
//        permission.setName(PermissionType.DELETE_TAGS);
//        assertEquals(PermissionType.DELETE_TAGS, permission.getName());
//    }
//
//    @Test
//    void testSetAndGetRoles() {
//        Permission permission = new Permission(PermissionType.READ_RESOURCES);
//        Set<Role> roles = new HashSet<>();
//        roles.add(new Role("ADMIN"));
//        roles.add(new Role("USER"));
//
//        permission.setRoles(roles);
//        assertEquals(2, permission.getRoles().size());
//    }
//
//    @Test
//    void testSetId() {
//        Permission permission = new Permission(PermissionType.CREATE_TAGS);
//        permission.setId(123L);
//        assertEquals(123L, permission.getId());
//    }
//
//    @Test
//    void testEqualsSameObject() {
//        Permission permission = new Permission(PermissionType.READ_RESOURCES);
//        assertEquals(permission, permission);
//    }
//
//    @Test
//    void testPermissionEquals() {
//        Permission perm1 = new Permission(PermissionType.UPDATE_OWN_RESOURCE);
//        Permission perm2 = new Permission(PermissionType.UPDATE_OWN_RESOURCE);
//        Permission perm3 = new Permission(PermissionType.CREATE_TAGS);
//
//        assertEquals(perm1, perm2);
//        assertEquals(perm1, perm2);
//        assertNotEquals(perm1, perm3);
//        assertNotEquals(perm1, null);
//        assertNotEquals(perm1, new Object());
//    }
//
//    @Test
//    void testPermissionHashCode() {
//        Permission perm1 = new Permission(PermissionType.UPDATE_OWN_RESOURCE);
//        Permission perm2 = new Permission(PermissionType.UPDATE_OWN_RESOURCE);
//        assertEquals(perm1.hashCode(), perm2.hashCode());
//    }
//
//    @Test
//    void testPermissionToString() {
//        Permission permission = new Permission(PermissionType.READ_RESOURCES);
//        assertEquals("Permission{id=null, name=READ_RESOURCES}", permission.toString());
//    }
}
