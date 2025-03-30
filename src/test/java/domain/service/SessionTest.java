package domain.service;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @AfterEach
    void tearDown() throws Exception {
        Field instanceField = Session.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void getInstance_ReturnsSingletonInstance() {
        Session instance1 = Session.getInstance();
        Session instance2 = Session.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2, "getInstance should always return the same instance");
    }

    @Test
    void getCurrentUser_InitiallyReturnsNull() {
        User currentUser = Session.getInstance().getCurrentUser();

        assertNull(currentUser, "Current user should be null");
    }

    @Test
    void setCurrentUser_SetsUserCorrectly() {
        Session session = Session.getInstance();
        User user = new User("Test", "User", "test@example.com", "password", new Role(RoleType.STUDENT));

        session.setCurrentUser(user);
        User retrievedUser = session.getCurrentUser();

        assertSame(user, retrievedUser, "get should return the user that was set");
    }

    @Test
    void logout_ClearsCurrentUser() {
        Session session = Session.getInstance();
        User user = new User("Test", "User", "test@example.com", "password", new Role(RoleType.STUDENT));
        session.setCurrentUser(user);

        session.logout();

        assertNull(session.getCurrentUser(), "Current user should be null");
    }

    @Test
    void getPermissionService_ReturnsNonNullService() {
        PermissionService permissionService = Session.getInstance().getPermissionService();

        assertNotNull(permissionService, "Permission service should not be null");
    }

    @Test
    void multipleOperations_MaintainsCorrectState() {
        Session session = Session.getInstance();
        User user1 = new User("User1", "Test", "user1@example.com", "password", new Role(RoleType.STUDENT));
        User user2 = new User("User2", "Test", "user2@example.com", "password", new Role(RoleType.TEACHER));

        session.setCurrentUser(user1);
        assertEquals("User1", session.getCurrentUser().getFirstName());

        session.setCurrentUser(user2);
        assertEquals("User2", session.getCurrentUser().getFirstName());

        session.logout();
        assertNull(session.getCurrentUser());
    }
}
