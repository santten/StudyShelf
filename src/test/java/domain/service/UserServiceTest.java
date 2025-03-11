package domain.service;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordService passwordService;
    private JWTService jwtService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        passwordService = Mockito.mock(PasswordService.class);
        jwtService = Mockito.mock(JWTService.class);
        userService = new UserService(userRepository, roleRepository, passwordService, jwtService);
    }

    @Test
    void testRegisterUser_Success() {
        Role studentRole = new Role(RoleType.STUDENT);
        when(roleRepository.findByName(RoleType.STUDENT)).thenReturn(studentRole);

        User testUser = new User("Alice", "Smith", "alice@example.com", "password123", studentRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User registeredUser = userService.registerUser("Alice", "Smith", "alice@example.com", "password123", RoleType.STUDENT);

        assertNotNull(registeredUser);
        assertEquals("Alice", registeredUser.getFirstName());
        assertEquals("Smith", registeredUser.getLastName());
        assertEquals("alice@example.com", registeredUser.getEmail());
        assertEquals(studentRole, registeredUser.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_ExistingRole() {
        Role existingRole = new Role(RoleType.TEACHER);
        when(roleRepository.findByName(RoleType.TEACHER)).thenReturn(existingRole);
        userService.registerUser("Bob", "Johnson", "bob@example.com", "securePass", RoleType.TEACHER);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testRegisterUser_NewRoleCreated() {
        when(roleRepository.findByName(RoleType.ADMIN)).thenReturn(null);
        Role newRole = new Role(RoleType.ADMIN);
        when(roleRepository.save(any(Role.class))).thenReturn(newRole);
        userService.registerUser("Charlie", "Brown", "charlie@example.com", "adminPass", RoleType.ADMIN);
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository, times(1)).save(roleCaptor.capture());
        assertEquals(RoleType.ADMIN, roleCaptor.getValue().getName());
    }

    @Test
    void testHashPassword() {
        String rawPassword = "TESTpassword123";
        String fakeHashedPassword = "$2a$10$ABCDEFG12345678901234abcdef1234567890abcdef1234567890abc";
        when(passwordService.hashPassword(rawPassword)).thenReturn(fakeHashedPassword);
        String hashedPassword = passwordService.hashPassword(rawPassword);
        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
    }

    @Test
    void testLoginUser_Success() {
        String email = "test@example.com";
        String rawPassword = "mypassword123";
        String hashedPassword = "hashedpassword";
        User user = new User("Test", "User", email, hashedPassword, new Role(RoleType.STUDENT));

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordService.checkPassword(rawPassword, hashedPassword)).thenReturn(true);
        when(jwtService.createToken(email)).thenReturn("mockJwtToken");

        String token = userService.loginUser(email, rawPassword);

        assertNotNull(token);
        assertEquals("mockJwtToken", token);
    }

    @Test
    void testUpdateUser_Success() {
        User user = new User("Old", "Name", "old@example.com", "password", new Role(RoleType.STUDENT));
        when(userRepository.update(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user, "New", "Name", "old@example.com");

        assertNotNull(updatedUser);
        assertEquals("New", updatedUser.getFirstName());
        assertEquals("Name", updatedUser.getLastName());
        assertEquals("old@example.com", updatedUser.getEmail());
        verify(userRepository).update(user);
    }

    @Test
    void testUpdateUser_EmailAlreadyTaken() {
        User existingUser = new User("Existing", "User", "taken@example.com", "password", new Role(RoleType.STUDENT));
        User userToUpdate = new User("Old", "Name", "old@example.com", "password", new Role(RoleType.STUDENT));

        when(userRepository.findByEmail("taken@example.com")).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userToUpdate, "New", "Name", "taken@example.com"));

        verify(userRepository).findByEmail("taken@example.com");
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void testUpdateUser_SameEmail() {
        User user = new User("Old", "Name", "same@example.com", "password", new Role(RoleType.STUDENT));
        when(userRepository.update(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user, "New", "Name", "same@example.com");

        assertNotNull(updatedUser);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository).update(user);
    }

    @Test
    void testUpdateUserPassword_Success() {
        User user = new User("Test", "User", "test@example.com", "oldPassword", new Role(RoleType.STUDENT));
        Session.getInstance().setCurrentUser(user);

        when(userRepository.changePassword(user.getUserId(), "oldPassword", "newPassword")).thenReturn(true);

        boolean result = userService.updateUserPassword(user, "oldPassword", "newPassword");

        assertTrue(result);
        verify(userRepository).changePassword(user.getUserId(), "oldPassword", "newPassword");
    }

    @Test
    void testUpdateUserPassword_NullPassword() {
        User user = new User("Test", "User", "test@example.com", "oldPassword", new Role(RoleType.STUDENT));

        Session.getInstance().setCurrentUser(user);
        userService.updateUserPassword(user, "oldPassword", null);

        assertEquals("oldPassword", user.getPassword());
        verify(passwordService, never()).hashPassword(any());
        verify(userRepository, never()).update(any());
    }

    @Test
    void testUpdateUserPassword_EmptyPassword() {
        User user = new User("Test", "User", "test@example.com", "oldPassword", new Role(RoleType.STUDENT));

        Session.getInstance().setCurrentUser(user);
        userService.updateUserPassword(user, "oldPassword", "");

        assertEquals("oldPassword", user.getPassword());
        verify(passwordService, never()).hashPassword(any());
        verify(userRepository, never()).update(any());
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String token = "valid-token";
        when(jwtService.getEmailFromToken(token)).thenReturn("test@example.com");

        boolean result = userService.isTokenValid(token);

        assertTrue(result);
        verify(jwtService).getEmailFromToken(token);
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        String token = "invalid-token";
        when(jwtService.getEmailFromToken(token)).thenReturn(null);

        boolean result = userService.isTokenValid(token);

        assertFalse(result);
        verify(jwtService).getEmailFromToken(token);
    }

    @Test
    void testCheckPassword() {
        String rawPassword = "userPassword";
        String hashedPassword = "hashedPassword";
        when(passwordService.checkPassword(rawPassword, hashedPassword)).thenReturn(true);

        boolean result = userService.checkPassword(rawPassword, hashedPassword);

        assertTrue(result);
        verify(passwordService).checkPassword(rawPassword, hashedPassword);
    }

    @Test
    void testFindByEmail() {
        String email = "test@example.com";
        User expectedUser = new User("Test", "User", email, "password", new Role(RoleType.STUDENT));
        when(userRepository.findByEmail(email)).thenReturn(expectedUser);


        User result = userService.findByEmail(email);

        assertEquals(expectedUser, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testDeleteUser() {
        User user = new User("Test", "User", "test@example.com", "password", new Role(RoleType.STUDENT));

        userService.deleteUser(user);

        verify(userRepository).delete(user);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User("User1", "Test", "user1@example.com", "password", new Role(RoleType.STUDENT));
        User user2 = new User("User2", "Test", "user2@example.com", "password", new Role(RoleType.TEACHER));
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(expectedUsers, result);
        verify(userRepository).findAll();
    }

    @Test
    void testUpdateUserFirstName_Success() {
        User user = new User("Old", "Name", "email@example.com", "password", new Role(RoleType.STUDENT));
        Session.getInstance().setCurrentUser(user);

        userService.updateUserFirstName(user, "NewFirstName");

        verify(userRepository).updateUserFirstName(user.getUserId(), "NewFirstName");
        assertEquals("NewFirstName", Session.getInstance().getCurrentUser().getFirstName());
    }

    @Test
    void testUpdateUserFirstName_NotAuthorized() {
        User user = new User("Old", "Name", "email@example.com", "password", new Role(RoleType.STUDENT));
        User anotherUser = new User("Another", "User", "another@example.com", "password", new Role(RoleType.STUDENT));
        user.setUserId(1231251);
        anotherUser.setUserId(146161);
        Session.getInstance().setCurrentUser(anotherUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUserFirstName(user, "NewFirstName"));

        assertEquals("You can't change someone else's first name unless you're an admin.", exception.getMessage());
        verify(userRepository, never()).updateUserFirstName(anyInt(), anyString());
    }

    @Test
    void testUpdateUserLastName_Success() {
        User user = new User("First", "OldLastName", "email@example.com", "password", new Role(RoleType.STUDENT));
        Session.getInstance().setCurrentUser(user);

        userService.updateUserLastName(user, "NewLastName");

        verify(userRepository).updateUserLastName(user.getUserId(), "NewLastName");
        assertEquals("NewLastName", Session.getInstance().getCurrentUser().getLastName());
    }

    @Test
    void testUpdateUserLastName_NotAuthorized() {
        User user = new User("First", "OldLastName", "email@example.com", "password", new Role(RoleType.STUDENT));
        User anotherUser = new User("Another", "User", "another@example.com", "password", new Role(RoleType.STUDENT));
        Session.getInstance().setCurrentUser(anotherUser);

        user.setUserId(1231251);
        anotherUser.setUserId(146161);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUserLastName(user, "NewLastName"));

        assertEquals("You can't change someone else's last name unless you're an admin.", exception.getMessage());
        verify(userRepository, never()).updateUserLastName(anyInt(), anyString());
    }

    @Test
    void testUpdateUserEmail_Success() {
        User user = new User("First", "Last", "old@example.com", "password", new Role(RoleType.STUDENT));
        Session.getInstance().setCurrentUser(user);

        userService.updateUserEmail(user, "new@example.com");

        verify(userRepository).updateUserEmail(user.getUserId(), "new@example.com");
        assertEquals("new@example.com", Session.getInstance().getCurrentUser().getEmail());
    }

    @Test
    void testUpdateUserEmail_NotAuthorized() {
        User user = new User("First", "Last", "old@example.com", "password", new Role(RoleType.STUDENT));
        User anotherUser = new User("Another", "User", "another@example.com", "password", new Role(RoleType.STUDENT));
        Session.getInstance().setCurrentUser(anotherUser);

        user.setUserId(1231251);
        anotherUser.setUserId(146161);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUserEmail(user, "new@example.com"));

        assertEquals("You can't change someone else's email unless you're an admin.", exception.getMessage());
        verify(userRepository, never()).updateUserEmail(anyInt(), anyString());
    }
}
