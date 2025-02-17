package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {
    @BeforeEach
    void testDefaultConstructor() {
        Role role = new Role();
        assertNotNull(role);
    }

    @Test
    void testRoleCreation() {
        Role role1 = new Role(RoleType.ADMIN);
        assertNotNull(role1);
        assertEquals(RoleType.ADMIN, role1.getName());

        Role role2 = new Role(RoleType.TEACHER);
        assertNotNull(role2);
        assertEquals(RoleType.TEACHER, role2.getName());

        Role role3 = new Role(RoleType.STUDENT);
        assertNotNull(role3);
        assertEquals(RoleType.STUDENT, role3.getName());
    }

    @Test
    void testAddPermissionsToRole() {
        Role role = new Role(RoleType.TEACHER);
        Permission permission = new Permission(PermissionType.READ_RESOURCES);
        role.getPermissions().add(permission);

        assertNotNull(role.getPermissions());
        assertTrue(role.getPermissions().contains(permission));
    }
//
//    @Test
//    void testAddPermissionToRole() {
//        Role role1 = new Role(RoleType.ADMIN);
//        assertNotNull(role1);
//        Permission permission = new Permission(PermissionType.CREATE_RESOURCE);
//        role1.getPermissions().add(permission);
//        assertTrue(role1.getPermissions().contains(permission));
//    }
//
//    @Test
//    void testRoleEquality() {
//        Role role1 = new Role(RoleType.ADMIN);
//        Role role2 = new Role(RoleType.TEACHER);
//        Role role3 = new Role(RoleType.STUDENT);
//        Role role4 = new Role(RoleType.STUDENT);
//        String notARole = "Not a Role";
//        assertEquals(role1, role1);
//        assertEquals(role2, role2);
//        assertEquals(role3, role4);
//        assertNotEquals(role1, role2);
//        assertNotEquals(role1, role3);
//        assertNotEquals(role1, null);
//        assertNotEquals(role1, notARole);
//    }
//
//    @Test
//    void testRoleToString() {
//        Role role = new Role("ADMIN");
//        assertEquals("Role{id=null, name='ADMIN'}", role.toString());
//    }
}
