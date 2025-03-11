package domain.service;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role(RoleType.STUDENT);
        testUser = new User("John", "Doe", "john.doe@example.com", "hashedPassword", testRole);
        testUser.setUserId(Integer.valueOf(3));
    }

    @Test
    void testRegisterUser_Success() {
        when(roleRepository.findByName(RoleType.STUDENT)).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);
        when(passwordService.hashPassword("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(Integer.valueOf(4));
            return user;
        });

        User registeredUser = userService.registerUser("John", "Doe", "john.doe@example.com", "password123", RoleType.STUDENT);

        assertNotNull(registeredUser);
        assertEquals("John", registeredUser.getFirstName());
        assertEquals("Doe", registeredUser.getLastName());
        assertEquals("john.doe@example.com", registeredUser.getEmail());
        assertEquals("hashedPassword", registeredUser.getPassword());
        assertEquals(testRole, registeredUser.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginUser_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);
        when(passwordService.checkPassword("password123", "hashedPassword")).thenReturn(Boolean.valueOf(true));
        when(jwtService.createToken("john.doe@example.com")).thenReturn("valid-token");

        String token = userService.loginUser("john.doe@example.com", "password123");

        assertNotNull(token);
        assertEquals("valid-token", token);
        verify(jwtService, times(1)).createToken("john.doe@example.com");
    }

    @Test
    void testLoginUser_Failure() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(null);

        String token = userService.loginUser("john.doe@example.com", "password123");

        assertNull(token);
        verify(jwtService, never()).createToken(anyString());
    }

    @Test
    void testFindByEmail_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);

        User foundUser = userService.findByEmail("john.doe@example.com");

        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void testFindByEmail_NotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        User foundUser = userService.findByEmail("unknown@example.com");

        assertNull(foundUser);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        List<User> users = userService.getAllUsers();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

//    @Test
//    void testUpdateUser_Success() {
//        when(userRepository.updateUserFields(3, "Updated", "User", "new.email@example.com")).thenReturn(testUser);
//
//        User updatedUser = userService.updateUser(3, "Updated", "User", "new.email@example.com");
//
//        assertNotNull(updatedUser);
//        assertEquals("Updated", updatedUser.getFirstName());
//        assertEquals("User", updatedUser.getLastName());
//        assertEquals("new.email@example.com", updatedUser.getEmail());
//
//        verify(userRepository, times(1)).updateUserFields(3, "Updated", "User", "new.email@example.com");
//    }
@Test
void testUpdateUser_Success() {
    when(userRepository.updateUserFields(3, "Updated", "User", "new.email@example.com"))
            .thenReturn(new User("Updated", "User", "new.email@example.com", "hashedPassword", testRole));

    User updatedUser = userService.updateUser(Integer.valueOf(3), "Updated", "User", "new.email@example.com");

    assertNotNull(updatedUser);
    assertEquals("Updated", updatedUser.getFirstName());
    assertEquals("User", updatedUser.getLastName());
    assertEquals("new.email@example.com", updatedUser.getEmail());

    verify(userRepository, times(1)).updateUserFields(3, "Updated", "User", "new.email@example.com");
}


    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).delete(testUser);

        userService.deleteUser(testUser);

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testUpdateUserPassword_Success() {
        when(passwordService.hashPassword("newPassword")).thenReturn("hashedNewPassword");

        userService.updateUserPassword(1, "newPassword");

        verify(userRepository, times(1)).updateUserPassword(1, "hashedNewPassword");
    }

    @Test
    void testUpdateUserPassword_EmptyPassword() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUserPassword(1, ""));

        assertEquals("Password cannot be empty.", exception.getMessage());
        verify(userRepository, never()).updateUserPassword(anyInt(), anyString());
    }

    @Test
    void testCheckPassword_Correct() {
        when(passwordService.checkPassword("password123", "hashedPassword")).thenReturn(Boolean.valueOf(true));

        boolean isValid = userService.checkPassword("password123", "hashedPassword");

        assertTrue(isValid);
    }

    @Test
    void testCheckPassword_Incorrect() {
        when(passwordService.checkPassword("wrongPassword", "hashedPassword")).thenReturn(Boolean.valueOf(false));

        boolean isValid = userService.checkPassword("wrongPassword", "hashedPassword");

        assertFalse(isValid);
    }
}
