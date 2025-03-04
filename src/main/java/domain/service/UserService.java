package domain.service;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordService passwordService, JWTService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    public User registerUser(String firstName, String lastName, String email, String password, RoleType roleType) {
        Role role = roleRepository.findByName(roleType);

        if (role == null) {
            role = new Role(roleType);
            role = roleRepository.save(role);
        }

        String hashedPassword = passwordService.hashPassword(password);
        User user = new User(firstName, lastName, email, password, role);
        return userRepository.save(user);
    }

    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordService.checkPassword(password, user.getPassword())) {
            return jwtService.createToken(email);
        }
        return null;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isTokenValid(String token) {
        return jwtService.getEmailFromToken(token) != null;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user, String firstName, String lastName, String email, String password) {
        if (email != null && !email.equals(user.getEmail()) && userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already taken!");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordService.hashPassword(password));
        }

        return userRepository.update(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
