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
    void testCheckPassword() {
        String rawPassword = "mypassword123";
        String fakeHashedPassword = "$2a$10$XYZ9876543210ABCDEFabcdef1234567890abcdef1234567890abcdef";

        when(passwordService.hashPassword(rawPassword)).thenReturn(fakeHashedPassword);
        when(passwordService.checkPassword(rawPassword, fakeHashedPassword)).thenReturn(true);
        when(passwordService.checkPassword("wrongpassword", fakeHashedPassword)).thenReturn(false);

        assertTrue(passwordService.checkPassword(rawPassword, fakeHashedPassword),"Matching passwords should return true");
        assertFalse(passwordService.checkPassword("wrongpassword", fakeHashedPassword),"Non-matching passwords should return false");
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
}
