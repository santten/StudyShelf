package domain.service;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;

import java.util.List;

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

    public User registerUser(String firstName, String lastName, String email,   String password, RoleType roleType) {
        Role role = roleRepository.findByName(roleType);

        if (role == null) {
            role = new Role(roleType);
            role = roleRepository.save(role);
        }

        String hashedPassword = passwordService.hashPassword(password);
        User user = new User(firstName, lastName, email, hashedPassword, role);
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

    public User updateUser(User user, String firstName, String lastName, String email) {
        if (email != null && !email.equals(user.getEmail()) && userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already taken!");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        return userRepository.update(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void updateUserFirstName(User user, String firstName) {
        User curUser = Session.getInstance().getCurrentUser();
        if (user.getUserId() != curUser.getUserId() && !curUser.isAdmin()) {
            throw new IllegalArgumentException("You can't change someone else's first name unless you're an admin.");
        }

        userRepository.updateUserFirstName(user.getUserId(), firstName);
        Session.getInstance().getCurrentUser().setFirstName(firstName);
    }

    public void updateUserLastName(User user, String lastName) {
        User curUser = Session.getInstance().getCurrentUser();
        if (user.getUserId() != curUser.getUserId() && !curUser.isAdmin()) {
            throw new IllegalArgumentException("You can't change someone else's last name unless you're an admin.");
        }

        userRepository.updateUserLastName(user.getUserId(), lastName);
        Session.getInstance().getCurrentUser().setLastName(lastName);
    }

    public void updateUserEmail(User user, String email) {
        User curUser = Session.getInstance().getCurrentUser();
        if (user.getUserId() != curUser.getUserId() && !curUser.isAdmin()) {
            throw new IllegalArgumentException("You can't change someone else's email unless you're an admin.");
        }

        userRepository.updateUserEmail(user.getUserId(), email);
        Session.getInstance().getCurrentUser().setEmail(email);
    }


    public boolean updateUserPassword(User user, String oldPassword, String newPassword) {
        if (user.getUserId() != Session.getInstance().getCurrentUser().getUserId()) {
            throw new IllegalArgumentException("You can only change your own password.");
        }

        return userRepository.changePassword(user.getUserId(), oldPassword, newPassword);
    }
}