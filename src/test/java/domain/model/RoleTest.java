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

    @BeforeEach
    void testRoleConstructor() {
        Role role = new Role("STUDENT");
        assertNotNull(role);
        assertEquals("STUDENT", role.getName());
    }

    @Test
    void testRoleCreation() {
        Role role = new Role("ADMIN");
        assertEquals("ADMIN", role.getName());
    }

    @Test
    void testRoleConstructorWithName() {
        Role role = new Role("TEACHER");
        assertNotNull(role);
        assertEquals("TEACHER", role.getName());
    }

    @Test
    void testRoleIdSetterGetter() {
        Role role = new Role();
        role.setId(123L);
        assertEquals(123L, role.getId());
    }

    @Test
    void testRoleSetNameGetter() {
        Role role = new Role();
        role.setName("TEACHER");
        assertEquals("TEACHER", role.getName());
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
        Role role3 = new Role("ADMIN");
        String notARole = "Not a Role";

        assertEquals(role1, role1);
        assertEquals(role1, role2);
        assertNotEquals(role1, role3);
        assertNotEquals(role1, null);
        assertNotEquals(role1, notARole);
    }

    @Test
    void testRoleToString() {
        Role role = new Role("ADMIN");
        assertEquals("Role{id=null, name='ADMIN'}", role.toString());
    }
}
