package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testRoleCreation() {
        Role role = new Role("ADMIN");
        assertEquals("ADMIN", role.getName());
    }

    @Test
    void testAddPermissionToRole() {
        Role role = new Role("TEACHER");
        Permission permission = new Permission(PermissionType.CREATE_RESOURCE);
        role.getPermissions().add(permission);
        assertTrue(role.getPermissions().contains(permission));
    }

    @Test
    void testRoleEquality() {
        Role role1 = new Role("STUDENT");
        Role role2 = new Role("STUDENT");
        assertEquals(role1, role2);
    }

    @Test
    void testRoleToString() {
        Role role = new Role("ADMIN");
        assertEquals("Role{id=null, name='ADMIN'}", role.toString());
    }
}
