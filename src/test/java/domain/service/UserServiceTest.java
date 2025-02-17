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

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        userService = new UserService(userRepository, roleRepository);
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
}
